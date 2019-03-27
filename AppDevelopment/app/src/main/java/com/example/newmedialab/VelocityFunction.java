package com.example.newmedialab;

import android.util.Log;

import java.util.Arrays;
import java.util.stream.IntStream;

public class VelocityFunction {

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
    int max_order;
    String[] simple_y_functions;


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

        Log.d("function = ", vel_function);
        for(int i = 0; i<order.length; i++){
            Log.d("y"+i+" = ", simple_y_functions[i]);
        }
/*
        String row;
        for(int i = 0; i<order.length; i++){
            row = "";
            for(int ij = 0; ij< order.length; ij++){
                row+= Integer.toString(who_inside_mat[i][ij]);
                row+= ", ";
            }
            Log.d("row "+i+" = ", row);
        }
*/


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
        Log.d("result ", result);







    }


    public Boolean testFunction() {
        return true;
    }


}


