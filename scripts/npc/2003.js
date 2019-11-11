
/* Author: Xterminator
	NPC Name: 		Robin
	Map(s): 		Maple Road : Snail Hunting Ground I (40000)
	Description: 		Beginner Helper
*/
var status;
var sel;

function start() {
    status = -1;
    sel = -1;
    cm.sendSimple("我可以告訴你些冒險者的技巧唷!!\r\n#L0##b要怎麼移動？#l\r\n#L1#我要如何擊退怪物？#l\r\n#L2#我要怎麼撿起物品？#l\r\n#L3#當我死掉會發生什麼事情？#l\r\n#L4#我何時能選擇職業？#l\r\n#L5#告訴我有關這個島嶼！#l\r\n#L6#我要怎麼做才能成為戰士？#l\r\n#L7#我要怎麼做才能成為弓箭手？#l\r\n#L8#我要怎麼做才能成為魔法師？#l\r\n#L9#我要怎麼做才能成為盜賊？#l\r\n#L10#怎麼提升能力值？(S)#l\r\n#L11#我要怎麼確認我撿起來的物品呢？#l\r\n#L12#我要怎麼裝備物品？#l\r\n#L13#我要怎麼確認我身上已經裝備的物品？#l\r\n#L14#什麼是技能？(K)#l\r\n#L15#我要怎麼前往維多利亞島？#l\r\n#L16#楓幣是什麼？#l#k");
}

function action(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (mode == 0 && type != 4)
            status -= 2;
        else {
            cm.dispose();
            return;
        }
    }
    if (status == 0) {
        if (sel == -1)
            sel = selection;
        if (sel == 0)
            cm.sendNext("好，我來教你如何移動。 使用 #方向左鍵#k 就能在平台上移動了，按下 #bAlt#k 可以進行跳躍。 有些鞋子能提升你的速度以及跳躍力。");
        else if (sel == 1)
            cm.sendNext("好，擊退怪物很簡單，每個怪物有自己的血條，你可以使用武器將他們殺死。當然，如果怪物等級越高，你越難擊退它們。");
        else if (sel == 2)
            cm.sendNext("接下來告訴你如何剪取物品，當你擊退怪物時，會有機會掉落寶物以及楓幣，當地上有物品時，按下#bZ#k 或是 數字鍵盤上的 #b0 來撿取物品。");
        else if (sel == 3)
            cm.sendNext("你好奇地找出當你死會發生什麼嗎？ 當你的HP歸零時，你會變成幽靈。 而地上會出現一塊墓碑，而你無法移動，但是你還是可以聊天。");
        else if (sel == 4)
            cm.sendNext("什麼時候你可以選擇你的職業？哈哈哈，別緊張，我的朋友啊～每個職業都有等級的限制。通常在8等和10等之間會進行。");
        else if (sel == 5)
            cm.sendNext("你想要知道這個島嶼嗎？ 這裏是楓之島，這座島嶼浮在天空上。由於浮在天空上，強大的怪物們無法靠近。這裏非常和平，非常適合新手。");
        else if (sel == 6)
            cm.sendNext("你想成為#b戰士#k？ 摁...那我建議你到維多利亞港，尋找一個叫做#r勇士之村#k的戰士村莊以及去找尋#bDances with Balrog#k。 他會教你如何成為一個戰士。 喔對了，有件很重要的事，你必須達到等級10才能成為戰士！");
        else if (sel == 7)
            cm.sendNext("You want to become a #bBowman#k? You'll need to go to Victoria Island to make the job advancement. Head over to a bowman-town called #rHenesys#k and talk to the beautiful #bAthena Pierce#k and learn the in's and out's of being a bowman. Ohh, and one VERY important thing: You'll need to be at least level 10 in order to become a bowman!!");
        else if (sel == 8)
            cm.sendNext("You want to become a #bMagician#k? For you to do that, you'll have to head over to Victoria Island. Head over to a magician-town called #rEllinia#k, and at the very top lies the Magic Library. Inside, you'll meet the head of all wizards, #bGrendel the Really Old#k, who'll teach you everything about becoming a wizard.");
        else if (sel == 9)
            cm.sendNext("You want to become a #bThief#k? In order to become one, you'll have to head over to Victoria Island. Head over to a thief-town called #rKerning City#k, and on the shadier side of town, you'll see a thief's hideaway. There, you'll meet #bDark Lord#k who'll teach you everything about being a thief. Ohh, and one VERY important thing: You'll need to be at least level 10 in order to become a thief!!");
        else if (sel == 10)
            cm.sendNext("You want to know how to raise your character's ability stats? First press #bS#k to check out the ability window. Every time you level up, you'll be awarded 5 ability points aka AP's. Assign those AP's to the ability of your choice. It's that simple.");
        else if (sel == 11)
            cm.sendNext("You want to know how to check out the items you've picked up, huh? When you defeat a monster, it'll drop an item on the ground, and you may press #bZ#k to pick up the item. That item will then be stored in your item inventory, and you can take a look at it by simply pressing #bI#k.");
        else if (sel == 12)
            cm.sendNext("You want to know how to wear the items, right? Press #bI#k to check out your item inventory. Place your mouse cursor on top of an item and double-click on it to put it on your character. If you find yourself unable to wear the item, chances are your character does not meet the level & stat requirements. You can also put on the item by opening the equipment inventory (#bE#k) and dragging the item into it. To take off an item, double-click on the item at the equipment inventory.");
        else if (sel == 13)
            cm.sendNext("You want to check on the equipped items, right? Press #bE#k to open the equipment inventory, where you'll see exactly what you are wearing right at the moment. To take off an item, double-click on the item. The item will then be sent to the item inventory.");
        else if (sel == 14)
            cm.sendNext("The special 'abilities' you get after acquiring a job are called skills. You'll acquire skills that are specifically for that job. You're not at that stage yet, so you don't have any skills yet, but just remember that to check on your skills, press #bK#k to open the skill book. It'll help you down the road.");
        else if (sel == 15)
            cm.sendNext("How do you get to Victoria Island? On the east of this island there's a harbor called Southperry. There, you'll find a ship that flies in the air. In front of the ship stands the captain. Ask him about it.");
        else if (sel == 16)
            cm.sendNext("It's the currency used in MapleStory. You may purchase items through mesos. To earn them, you may either defeat the monsters, sell items at the store, or complete quests...");
    } else if (status == 1) {
        if (sel == 0)
            cm.sendNextPrev("In order to attack the monsters, you'll need to be equipped with a weapon. When equipped, press #bCtrl#k to use the weapon. With the right timing, you'll be able to easily take down the monsters.");
        else if (sel == 1)
            cm.sendNextPrev("Once you make the job advancement, you'll acquire different kinds of skills, and you can assign them to HotKeys for easier access. If it's an attacking skill, you don't need to press Ctrl to attack, just press the button assigned as a HotKey.");
        else if (sel == 2)
            cm.sendNextPrev("Remember, though, that if your item inventory is full, you won't be able to acquire more. So if you have an item you don't need, sell it so you can make something out of it. The inventory may expand once you make the job advancement.");
        else if (sel == 3)
            cm.sendNextPrev("There isn't much to lose when you die if you are just a beginner. Once you have a job, however, it's a different story. You'll lose a portion of your EXP when you die, so make sure you avoid danger and death at all cost.");
        else if (sel == 4)
            cm.sendNextPrev("Level isn't the only thing that determines the advancement, though. You also need to boost up the levels of a particular ability based on the occupation. For example, to be a warrior, your STR has to be over 35, and so forth, you know what I'm saying? Make sure you boost up the abilities that has direct implications to your job.");
        else if (sel == 5)
            cm.sendNextPrev("But, if you want to be a powerful player, better not think about staying here for too long. You won't be able to get a job anyway. Underneath this island lies an enormous island called Victoria Island. That place is so much bigger than here, it's not even funny.");
        else if (sel == 8)
            cm.sendNextPrev("Oh by the way, unlike other jobs, to become a magician you only need to be at level 8. What comes with making the job advancement early also comes with the fact that it takes a lot to become a true powerful mage. Think long and carefully before choosing your path.");
        else if (sel == 10)
            cm.sendNextPrev("Place your mouse cursor on top of all abilities for a brief explanation. For example, STR for warriors, DEX for bowman, INT for magician, and LUK for thief. That itself isn't everything you need to know, so you'll need to think long and hard on how to emphasize your character's strengths through assigning the points.");
        else if (sel == 15)
            cm.sendNextPrev("Oh yeah! One last piece of information before I go. If you are not sure where you are, always press #bW#k. The world map will pop up with the locator showing where you stand. You won't have to worry about getting lost with that.");
        else
            start();
    } else
        start();
}