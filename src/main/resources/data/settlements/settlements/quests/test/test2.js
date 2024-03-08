require('settlements:scripts/includes/click')

function canStart() {
    print("This function was called from test2.js");
    return true;
}

var counter = 0;

function getItem(player) {
    var ret = random(0, 25);
    //print('Random: ' + ret);
    //quest.complete(player);
    counter++;
    if (counter >= 10) {
        quest.complete(player);
    }

    print(counter);

  return ret == 0 ? 'minecraft:stone' : 'minecraft:cobblestone';
}

//Save the data between sessions?
function saveData(tag) {
    tag.save('counter', counter);
}

function loadData(tag) {
    print("is this even called?")
    counter = tag.load('counter', 0);
}