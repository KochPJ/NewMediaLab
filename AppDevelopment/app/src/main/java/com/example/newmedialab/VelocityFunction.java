package com.example.newmedialab;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class VelocityFunction implements Serializable {

    String vel_function = "";
    char[] vel_function_char;
    int n_ob = 0;
    int n_cb = 0;
    int[] ob;
    int[] cb;
    int order[];
    int order_ys[];
    int parts[][];
    int who_inside_mat[][];
    int y_length = 0;
    int max_order;
    String[] simple_y_functions;
    double[] y_results;
    String x_result;
    double[] xd;
    double[] x;


    public VelocityFunction(String velocity_function){
        vel_function = velocity_function;
        vel_function_char = vel_function.toCharArray();
        buildFunction();

    }

    public void buildFunction(){
        //check for brackets
        for (int i = 0; i < vel_function_char.length; i++) {
            char c = vel_function_char[i];
            if(c=='('){
                n_ob++;
            }else if(c==')'){
                n_cb++;
            }
        }

        // fill the array with the bracket index
        ob = new int[n_ob];
        cb = new int[n_cb];
        int j = 0;
        int k = 0;
        for (int i = 0; i < vel_function_char.length; i++) {
            char c = vel_function_char[i];
            if(c=='('){
                ob[j] = i;
                j++;
            }else if(c==')'){
                cb[k] = i;
                k++;
            }
        }

        parts = new int[ob.length][2];

        for(int i = 0; i < ob.length; i++) {
            int start = ob[i];
            int end = 0;
            for(int p = 0; p < cb.length; p++){
                int stop = cb[p];
                int z_ob = 0;
                int z_cb = 0;
                for(int z = 0; z < ob.length; z++){
                    if((ob[z]> start)&(ob[z]<stop)){
                        z_ob++;
                    }
                    if((cb[z]> start)&(cb[z]< stop)){
                        z_cb++;
                    }
                }
                if((z_cb==z_ob)&(start<cb[p])){
                   end = cb[p];
                   p = ob.length;
                }
                }
            parts[i][0] = start;
            parts[i][1] = end;
        }

        order = new int[ob.length];
        max_order = 0;

        who_inside_mat = new int[ob.length][ob.length];


        //loop through max depth
        for(int d= 0; d < ob.length; d++){
            //loop through parts
            for(int i= 0; i < ob.length; i++){
                //loop through parts again
                for(int p = 0; p < ob.length; p++) {
                    //check if the given part p is inside the part i
                    if ((parts[i][0] < parts[p][0]) & (parts[i][1] > parts[p][1])) {
                        //increase counter if the part p is inside the part i
                        who_inside_mat[i][p] = 1;
                    }
                }

                //get how many parts are in the part i
                int c_inside = 0;
                for(int in : who_inside_mat[i])
                    c_inside+=in;
                // get the index of the part which are in the part i
                int[] who_inside_int = new int[c_inside];
                int cc = 0;
                for(int pos = 0; pos < who_inside_mat[i].length; pos ++){
                    if (who_inside_mat[i][pos]==1){
                        who_inside_int[cc] = pos;
                        cc++;
                    }
                }

                //determine the current order
                int current_order = 0;
                int c_o;
                for(int c_n = 0; c_n<who_inside_int.length; c_n++){
                    c_o = order[who_inside_int[c_n]]+1;
                    if (c_o>current_order){
                        current_order = c_o;
                    }
                }

                //check if the part i does belong to the order d
                if(current_order == d){
                    //put the part i into the order array and increase the order counter by 1
                    order[i] = current_order;
                    max_order = d;
                }
            }
        }


        order_ys = new int[order.length];
        int o_ys = 0;
        simple_y_functions = new String[order.length];
        int s_c = 0;
        String function_y;
        char[] function_y_char;
        String function_y_simple;


        //loop through the dept of the parts
        for(int i= 0; i < max_order+1; i++){
            //loop through the orders in order
            for(int or = 0; or < order.length; or++){
                function_y = "";
                function_y_simple = "";
                //check if the current order does belong to the current depth i
                if (order[or] == i){

                    //set the given y index for the given part
                    order_ys[or] = o_ys;
                    o_ys++;

                    //put the chars to a string of the current function
                    int start = parts[or][0]+1;
                    int end = parts[or][1];
                    for(int l = start; l< end; l++){
                        function_y += vel_function_char[l];
                    }
                    //given that i>0 so the depth is bigger then 0 we can use functions of the depth i-1 to represent the functions of the depth i
                    if(i>0){
                        int inside = 0;
                        //loop through the who inside mat to find which i-1 functions are inside the current i
                        for(int p = 0; p < who_inside_mat[or].length; p++ ){
                            //check if the function is inside
                            if(who_inside_mat[or][p]==1){
                                //check if the given function is of depth i-1
                                if(order[p] == i-1){
                                    //increase a counter so we can ini the arrays we need
                                    inside++;
                                }
                            }
                        }
                        //ini some needed arrays
                        int[] starts = new int[inside];
                        int[] stops = new int[inside];
                        int[] ys = new int[inside];
                        int n = 0;
                        //loop through the who inside mat again
                        for(int p = 0; p < who_inside_mat[or].length; p++ ){
                            //check again which parts are inside the current part
                            if(who_inside_mat[or][p]==1){
                                //check if the parts are also of type i-1
                                if(order[p] == i-1){
                                    //get the start and stop positions such that we can cut them out and replace with a different representation
                                    stops[n] = parts[p][0]-start;
                                    starts[n] = parts[p][1]-start+1;
                                    ys[n] = order_ys[p];
                                    n++;
                                }
                            }
                        }

                        function_y_char = function_y.toCharArray();
                        function_y_simple = "";
                        int begin = 0;
                        int last = function_y_char.length;
                        for(int s = 0; s< inside; s++){
                            int stop = stops[s];
                            for(int b = begin; b<stop; b++){
                                function_y_simple+= function_y_char[b];
                            }
                            function_y_simple += 'y';
                            function_y_simple += Integer.toString(ys[s]);
                            begin = starts[s];
                        }
                        for(int b = begin; b<last; b++){
                            function_y_simple+= function_y_char[b];
                        }
                    //if the depth is 0 then the function is already at the simples version
                    }else{
                        function_y_simple = function_y;
                    }
                    simple_y_functions[s_c] = function_y_simple;
                    s_c++;
                }
            }
        }
        y_length = simple_y_functions.length;


        int col;
        int c_col = 0;
        for(int c = 0; c<order.length; c++){
            col = 0;
            for(int r = 0; r< order.length; r++){
                col+= who_inside_mat[r][c];
            }
            if(col == 0){
                c_col++;
            }
        }
        int[] end_col = new int[c_col];
        c_col = 0;
        for(int c = 0; c<order.length; c++){
            col = 0;
            for(int r = 0; r< order.length; r++){
                col+= who_inside_mat[r][c];
            }
            if(col == 0){
                end_col[c_col] = c;
                c_col++;
            }
        }

        String result = "";
        int begin = 0;
        int last = vel_function_char.length;
        for(int i = 0; i< c_col; i++){
            int stop = parts[end_col[i]][0];
            int start = parts[end_col[i]][1]+1;
            String end_function = "y"+Integer.toString(order_ys[end_col[i]]);

            for(int b = begin; b<stop; b++){
                result+= vel_function_char[b];
            }
            char[] end_function_char = end_function.toCharArray();
            for(int b = 0; b<end_function_char.length; b++){
                result+=end_function_char[b];
            }
            begin = start;
        }
        for(int b = begin; b<last; b++){
            result+= vel_function_char[b];
        }
        x_result = result;
    }

    public double compute_xd(double x) {

        //ini results array
        y_results = new double[this.simple_y_functions.length];

        //solve all y
        for(int i = 0; i < this.simple_y_functions.length; i++){
            //lifts all the math functions and returns a single value for the given y
            String current_y = this.simple_y_functions[i];
            List<String> operations_list = createOperationList(current_y);
            y_results[i] = getCalcResult(operations_list, x);
        }

        List<String> operations_list = createOperationList(x_result);
        double xd = getCalcResult(operations_list, x);
        String a= "";
        a+= Double.valueOf(xd);

        String b= "";
        b+= Double.valueOf(x);
        //Log.d("Derivative","the derivative at x = "+b+" is  xd = "+a );
        return xd;
    }

    @SuppressLint("LongLogTag")
    public double getCalcResult(List<String> operations_list, double x){

        Double[] calculations; // this one holds the calculations (the numbers)
        Double[] calculations_index; // this one has 1 if the index is a number and 0 if the index is not a number.
        // needed because we have to know which index of the calculation array is actually a number. since they can be 0 sometimes. e.g if x = 0 or y_i = 0.
        // ini the calculation array with 0.0
        calculations = new Double[operations_list.size()];
        calculations_index = new Double[operations_list.size()];
        for(int k = 0; k < calculations.length; k++){
            calculations[k] = 0.0;
            calculations_index[k]= 0.0;
        }

        // fill the calculation array with the numbers we have. x,y,n
        String op = "";
        for(int k = 0; k < operations_list.size(); k++){
            op = operations_list.get(k);
            if(number(op.charAt(0))){
                calculations[k] = Double.parseDouble(op); // put the real number
                calculations_index[k] = 1.0; //but in the boolean so we know that at pos k there is a number
            }else if(x(op.charAt(0))){
                calculations[k] = x;
                calculations_index[k] = 1.0;
            }else if(y(op.charAt(0))){
                // make a for loop to get the number behind the y. since it can be 1 or 2 even 3 digits i use a for loop
                String y_n = "";
                char[] y_n_char = op.toCharArray();
                for(int p = 1; p<y_n_char.length;p++){
                    y_n += y_n_char[p];
                }
                int y_n_int = Integer.valueOf(y_n);
                calculations[k] = y_results[y_n_int];
                calculations_index[k] = 1.0;
            }
        }

        // first we lift the powers.
        for(int k = 0; k < operations_list.size(); k++){
            op = operations_list.get(k);
            if(power(op.charAt(0))){
                double value_left = 0.0;
                double value_right = 0.0;
                for(int l = k-1; l >-1; l--){
                    double v = calculations[l];
                    double v_i = calculations_index[l];
                    //check for the next value to the left where my boolean array shows me a value
                    if(v_i != 0.0){
                        value_left = v; //but the left value to the current value or l
                        calculations[l] = 0.0; //put the value to 0
                        calculations_index[l] = 0.0; // put the index to 0
                        l = -1; // break the for loop
                    }
                }
                for(int r = k+1; r <calculations.length; r++){
                    //check for the next value to the right where my boolean array shows me a value
                    if(calculations_index[r] != 0.0){
                        value_right = calculations[r]; //but the right value to the current value or r
                        calculations[r] = 0.0; //put the value to 0
                        calculations_index[r] = 0.0; // put the index to 0
                        r = calculations.length; // break the for loop
                    }
                }
                calculations_index[k] = 1.0; //but the boolean to true where i store my new result
                calculations[k] = power(value_left,value_right); //put in the new result into the k positon
            }
        }

        //lift the functions exp, log, sin, cos
        for(int k = 0; k < operations_list.size(); k++){
            op = operations_list.get(k);
            if(func(op.charAt(0))){
                double value_right = 0.0;
                for(int r = k+1; r <calculations.length; r++){
                    //check for the next value to the right where my boolean array shows me a value
                    if(calculations_index[r] != 0.0){
                        value_right = calculations[r]; //but the right value to the current value or r
                        calculations[r] = 0.0; //put the value to 0
                        calculations_index[r] = 0.0; // put the index to 0
                        r = calculations.length; // break the for loop
                    }
                }
                calculations_index[k] = 1.0; //but the boolean to true where i store my new result
                //compute the result for position k
                if(op.equals("e")){
                    calculations[k] = exp(value_right);
                }else if(op.equals("l")){
                    calculations[k] = log(value_right);
                }else if(op.equals("s")){
                    calculations[k] = sin(value_right);
                }else if(op.equals("c")){
                    calculations[k] = cos(value_right);
                }
            }
        }

        // lift multiplications (include * and /)
        for(int k = 0; k < operations_list.size(); k++){
            op = operations_list.get(k);
            if(multiplication(op.charAt(0))){
                double value_left = 0.0;
                double value_right = 0.0;
                for(int l = k-1; l >-1; l--){
                    double v = calculations[l];
                    double v_i = calculations_index[l];
                    //check for the next value to the left where my boolean array shows me a value
                    if(v_i != 0.0){
                        value_left = v; //but the left value to the current value or l
                        calculations[l] = 0.0; //put the value to 0
                        calculations_index[l] = 0.0; // put the index to 0
                        l = -1; // break the for loop
                    }
                }
                for(int r = k+1; r <calculations.length; r++){
                    //check for the next value to the right where my boolean array shows me a value
                    if(calculations_index[r] != 0.0){
                        value_right = calculations[r]; //but the right value to the current value or r
                        calculations[r] = 0.0; //put the value to 0
                        calculations_index[r] = 0.0; // put the index to 0
                        r = calculations.length; // break the for loop
                    }
                }
                calculations_index[k] = 1.0; //but the boolean to true where i store my new result
                //compute the result for position k
                if(op.equals("*")){
                    calculations[k] = multiply(value_left,value_right);
                }else if(op.equals("/")){
                    calculations[k] = devide(value_left, value_right);
                }
            }
        }


        // lift addition (include - and +)
        for(int k = 0; k < operations_list.size(); k++){
            op = operations_list.get(k);
            if(addition(op.charAt(0))){
                double value_left = 0.0;
                double value_right = 0.0;
                for(int l = k-1; l >-1; l--){
                    double v = calculations[l];
                    double v_i = calculations_index[l];
                    //check for the next value to the left where my boolean array shows me a value
                    if(v_i != 0.0){
                        value_left = v; //but the left value to the current value or l
                        calculations[l] = 0.0; //put the value to 0
                        calculations_index[l] = 0.0; // put the index to 0
                        l = -1; // break the for loop
                    }
                }
                for(int r = k+1; r <calculations.length; r++){
                    //check for the next value to the right where my boolean array shows me a value
                    if(calculations_index[r] != 0.0){
                        value_right = calculations[r]; //but the right value to the current value or r
                        calculations[r] = 0.0; //put the value to 0
                        calculations_index[r] = 0.0; // put the index to 0
                        r = calculations.length; // break the for loop
                    }
                }
                calculations_index[k] = 1.0; //but the boolean to true where i store my new result
                //compute the result for position k
                if(op.equals("+")){
                    calculations[k] = plus(value_left,value_right);
                }else if(op.equals("-")){
                    calculations[k] = minus(value_left, value_right);
                }
            }
        }

        // their can only be one value left int he array. Search it and return it
        double calc_result = 0.0;
        for(int k = 0; k < calculations.length; k++){
            if (calculations[k] != 0.0){
                calc_result = calculations[k];
            }
        }

        return  calc_result;
    }


    public Boolean number(char c){
        char[] a = {'0','1','2','3','4','5','6','7','8','9','.'};
        Boolean n = false;
        for(int i = 0; i < a.length; i++){
            if(a[i] == c){
                n = true;
            }
        }
        return n;
    }

    public Boolean x(char c){
        return ('x' == c);
    }

    public Boolean y(char c){
        return ('y' == c);
    }
    public Boolean func(char c){
        char[] a = {'e','l','s','c'};
        Boolean n = false;
        for(int i = 0; i < a.length; i++){
            if(a[i] == c){
                n = true;
            }
        }
        return n;
    }

    public Boolean power(char c){
        return ('^' == c);
    }

    public Boolean addition(char c){
        char[] a = {'+','-'};
        Boolean n = false;
        for(int i = 0; i < a.length; i++){
            if(a[i] == c){
                n = true;
            }
        }
        return n;
    }

    public Boolean multiplication(char c){
        char[] a = {'*','/'};
        Boolean n = false;
        for(int i = 0; i < a.length; i++){
            if(a[i] == c){
                n = true;
            }
        }
        return n;
    }


    public Boolean testFunction(int n) {
        x = new double[n];
        xd = new double[n];
        Boolean working = true;
        for(int i =0; i < n; i++){
            x[i] = i*10.0/(n-1);
            xd[i] = compute_xd(x[i]);
            if(xd[i] < 0.01){
                Log.d("derivative is to small", "at i = "+Integer.toString(i));
                i = n;
                working = false;
            }
        }

        return working;
    }

    public double power(double a, double b){
        return Math.pow(a, b);
    }

    public double sin(double a){
        return Math.sin(a);
    }

    public double cos(double a){
        return Math.cos(a);
    }

    public double log(double a){
        return Math.log(a);
    }

    public double exp(double a){
        return Math.exp(a);
    }

    public double plus(double a, double b){
        return a+b;
    }

    public double minus(double a, double b){
        return a-b;
    }

    public double devide(double a, double b){
        return a/b;
    }

    public double multiply(double a, double b){
        return a*b;
    }

    public List<String> createOperationList(String current_y) {
        char[] current_y_char = current_y.toCharArray();;
        String current_piece = "";
        List<String> operations_list = new ArrayList<String>();

        //get the current funciton y
        //convert to char array
        for (int j = 0; j < current_y_char.length; j++) {
            char c = current_y_char[j];
            if (number(c)) {
                current_piece += c;
                for (int k = j + 1; k < current_y_char.length; k++) {
                    char c_k = current_y_char[k];
                    if (number(c_k)) {
                        current_piece += c_k;
                        j++;
                    } else {
                        operations_list.add(current_piece);
                        current_piece = "";
                        k = current_y_char.length;
                    }
                }
                if (j == current_y_char.length - 1) {
                    operations_list.add(current_piece);
                    current_piece = "";
                }
            } else if (x(c)) {
                operations_list.add(String.valueOf(c));
            } else if (y(c)) {
                String c_y = "y";
                for (int k = j + 1; k < current_y_char.length; k++) {
                    char c_k = current_y_char[k];
                    if (number(c_k)) {
                        c_y += c_k;
                        j++;
                    } else {
                        k = current_y_char.length;
                        operations_list.add(c_y);
                    }
                    if (k == current_y_char.length - 1) {
                        k = current_y_char.length;
                        operations_list.add(c_y);
                    }
                }

            } else if (func(c)) {
                operations_list.add(String.valueOf(c));
                j += 2;
            } else if (power(c)) {
                operations_list.add(String.valueOf(c));
            } else if (addition(c)) {
                operations_list.add(String.valueOf(c));
            } else if (multiplication(c)) {
                operations_list.add(String.valueOf(c));
            }
        }

        return operations_list;
    }

}


