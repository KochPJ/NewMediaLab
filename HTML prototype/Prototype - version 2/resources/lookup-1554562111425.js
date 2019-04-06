(function(window, undefined) {
  var dictionary = {
    "ccd9387e-f04a-4634-8908-8ac53ee39581": "Edit test",
    "a7cb14f6-ec3d-4cf2-895d-5475a4ae2240": "View stimuli",
    "8e258d98-b9cc-4e77-b94a-62be687a411e": "Test MC question - 2",
    "f94fcbe9-9976-4e59-89cf-52452ea7e145": "Start",
    "506a7cc4-4c63-4730-ac47-8bd256e9320c": "Create new experiment",
    "f7d15729-a599-4d21-b4ea-5e8795fe63de": "Edit test - writing sample",
    "4b6dd966-3d18-433c-a1d7-8fc00bcc802c": "Test - done",
    "ae7c2346-c211-4bdc-9407-2916a1294c87": "Edit test - writing",
    "5c97d7ca-1a89-4048-b9ae-15964615ae34": "Edit video - sample",
    "9706ace2-d9df-4da6-b366-6b0a7d5926ca": "Add stimuli - 2",
    "67a40d5e-f0f3-448a-bd8e-5075f051e1b2": "Test MC question",
    "95c928cd-4aec-4178-82a4-612b64eace86": "Edit test - MC - 2",
    "8cbb4f2a-f2a5-4822-af2b-b6763506b1b1": "Edit test - MC - 1",
    "8a242192-1e87-47d9-a66c-7b16cb0ee375": "Select participant",
    "6ac60667-37b0-4a50-a4db-6365e5ae9cf8": "My experiments",
    "a5517f1f-4eca-472c-900a-0b45571ab404": "Statistics - extract",
    "5ca327ad-fe96-4d57-ae56-f3817691965f": "Test - video",
    "0260ca3c-52e0-4ea9-a999-3ce0a00fb47b": "Statistics",
    "506ca8b3-dbe9-42b1-a125-9bbb5861f3eb": "Edit video - 3",
    "dd7afbf7-962b-4d86-913c-e28e96f03750": "Edit video - 2",
    "9f20e877-26ec-4858-bc1f-150037e39220": "Add stimuli",
    "10d779cc-7326-4b54-8453-8e6802fcaf10": "Edit video - 1",
    "b35718ba-d2d3-43ef-bfab-df2cd6bd0390": "Participant info",
    "88b2198f-0ad3-4908-9390-322c48275f1b": "Start experiment",
    "b1a23562-027d-44c3-99bc-20ddd0ac6489": "Test - writing",
    "f39803f7-df02-4169-93eb-7547fb8c961a": "Template 1",
    "850cff1e-2a72-4bfb-ab6a-c8f651d812bd": "stimuli",
    "b89c5f3d-f19a-4b5f-af2b-40725ae3434c": "start",
    "f21d4c43-d4da-4501-b1a2-509a518dd1f3": "editing video",
    "bb8abf58-f55e-472d-af05-a7d1bb0cc014": "default",
    "e115de26-af01-445a-9ba4-e30fd81547db": "start test",
    "8627fccd-dab1-4b94-983a-dfc033d85cb1": "editing test",
    "9722ffec-c611-46cf-8e39-fc4c48be2d54": "statistics"
  };

  var uriRE = /^(\/#)?(screens|templates|masters|scenarios)\/(.*)(\.html)?/;
  window.lookUpURL = function(fragment) {
    var matches = uriRE.exec(fragment || "") || [],
        folder = matches[2] || "",
        canvas = matches[3] || "",
        name, url;
    if(dictionary.hasOwnProperty(canvas)) { /* search by name */
      url = folder + "/" + canvas;
    }
    return url;
  };

  window.lookUpName = function(fragment) {
    var matches = uriRE.exec(fragment || "") || [],
        folder = matches[2] || "",
        canvas = matches[3] || "",
        name, canvasName;
    if(dictionary.hasOwnProperty(canvas)) { /* search by name */
      canvasName = dictionary[canvas];
    }
    return canvasName;
  };
})(window);