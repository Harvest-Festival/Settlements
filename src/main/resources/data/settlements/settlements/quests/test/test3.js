require('settlements:scripts/includes/click')

function canStart(player, tracker) {
    return tracker.completed("settlements:test/test2");
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
    player.kill();

  return ret == 0 ? 'minecraft:emerald' : 'minecraft:diamond';
}


/** Save the data */
function saveData(tag) {
    tag.save('counter', counter);
}

/** Load the data */
function loadData(tag) {
    counter = tag.load('counter', 0);
}