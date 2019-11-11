var status = 0;
var qChars = new Array("Q1: 楓之谷中，從等級1到等級2需要多少經驗值?#10#12#15#20#3",
    "Q1: 根據不同職業為了第1次轉職所要求的能力，被不正確敍述的是哪一個?#劍士 35 力量以上#盜賊 20 幸運以上#法師 20 智力以上#弓箭手 25 敏捷以上#2",
    "Q1: 被怪物攻擊時特別的異常狀態沒有被正確說明的是哪一個?#虛弱 - 移動速度降低#封鎖 - 不能使用技能#黑暗 - 精準降低#詛咒 - 經驗減少#1",
    "Q1: 下列哪一個攻擊怪物說明不正確?#冰凍 - 更多傷害施予火屬性怪物上#火焰 - 更多傷害施予冰屬性怪物上#聖光 - 更多傷害施予黑暗怪物上#毒 - 更多傷害到BOSS上#4",
    "Q1: 根據不同職業的第1次轉職必須條件,被正確敍述的是哪一個?#劍士 - 力量40以上#弓箭手 - 敏捷25以上#法師 - 智力30以上#盜賊 - 敏捷25以上#2");
var qItems = new Array("Q2: 下列怪物中，哪組怪物與打倒它所能得到的戰利品是正確對應關係的?#小幽靈 - 力量母礦#蝙蝠 - 蝙蝠翅膀#綠水靈 - 彈丸#肥肥 - 紅色緞帶#2",
    "Q2: 下列怪物中，哪組怪物與打倒它所能得到的戰利品是不正確對應關係的?#緞帶肥肥 - 紅色緞帶#綠水靈 - 綠水靈株#嫩寶 - 嫩寶殼#食人花 - 食人花的葉子#4",
    "Q2: 楓之谷下列藥品中，哪組藥品與功效是正確對應關係的?#白色藥水 - 恢復 250 HP#活力藥水 - 恢復 400 MP#紅色藥水 - 恢復 100 HP#披薩餅 - 恢復 400 HP#4",
    "Q2: 楓之谷下列藥品中，哪組藥品與功效是不正確對應關係的?#特殊藥水 - 恢復 50% HP和MP #超級藥水 - 恢復 100% HP和MP#薑汁汽水 - 恢復 75% HP和MP#清晨之露 - 3000 MP恢復#4",
    "Q2: GM活動中，能得到幾個水果鮮奶蛋糕?#7個#4個#5個#6個#3");
var qMobs = new Array("Q3: 綠菇菇、木妖、藍水靈、斧木妖，哪個是等級最高的怪物?#綠菇菇#木妖#藍水靈#斧木妖#4",
    "Q3: 楓之島沒有哪個怪物?#肥肥#嫩寶#藍寶#菇菇寶貝#1",
    "Q3: 維多利亞島沒有哪個怪物?#石球#綠水靈#黑木妖#鋼鐵肥肥#1",
    "Q3: 在艾納斯島沒有哪個怪物?#小黑狼#雪吉拉#提力#黑鱷魚#4",
    "Q3: 會飛的怪物是什麼?#巫婆#刺菇菇#狼人#企鵝#1",
    "Q3: 天空之城不會出現哪些怪物?#小黃獨角獅#小紫獨角獅#日光精靈#鱷魚#4",
    "Q3: 去天空之城的船上會出現哪個怪物?#綠水靈#地域巴洛谷#肥肥#木妖#2");
var qQuests = new Array("Q4: 下列哪個任務需要殺死50隻木妖#木妖好可怕#皮奧資源回收#約翰的粉紅花籃#珍的第一個挑戰#1",
    "Q4: 可以重複執行的任務是?#找回楓之谷古書#我好無聊#明明夫人的第一個苦惱#艾溫的玻璃鞋#4",
    "Q4: 下列哪個職業不是2轉中出現的職業#法師#狂戰士#弩弓手#刺客#1",
    "Q4: 下列哪個任務需要收集雞蛋?#蒐集狂麥森#奈咪的小菜料理材料#幽靈派溫#艾利遜的藥材#2",
    "Q4: 喚醒麥吉的舊戰劍不需要的材料是什麼?#古代卷軸#妖精之翼#舊戰劍#火焰羽毛#2",
    "Q4: 下列要求等級最高的任務是?#阿爾卡斯特和黑暗水晶#收集毛皮衣材料#與葛雷的交易#保護奈洛#1");
var qTowns = new Array("Q5: 在維多利亞島沒有的村落是?#弓箭手村#勇士之村#墮落城市#楓葉村#4",
    "Q5: 下列哪一個地區是屬於艾納斯島?#玩具城#奇幻村#冰原雪域#昭和村#3",
    "Q5: 在艾納斯島的冰原雪域看不見的NPC是誰?#管家艾瑪#高登#史匹奈爾#楓之谷GM#1",
    "Q5: 在維多利亞的弓箭手村看不見的NPC是誰?#長老斯坦#明明夫人#比休斯#麗娜#3",
    "Q5: 從楓之谷一開始，遇到的第一個NPC是誰?#皮奧#娜塔莎#江西男#希娜#4",
    "Q5: 弓箭手村的瑪亞請求我們拿什麼物品給她，來治好她的病?#奇怪的藥#奇怪的水#奇怪的你#奇怪的葯#1");
var correctAnswer = 0;

function start() {
    if (cm.haveItem(4031058, 1)) {
        cm.sendOk("#h #,你已經有了 #t4031058# 不要讓廢我時間.");
        cm.dispose();
    }
    if (!(cm.haveItem(4031058, 1))) {
        cm.sendNext("歡迎光臨 #h #, 我是 #p2030006#.\r\n看來你已經走了很遠到達了這個階段.");
    }
}

function action(mode, type, selection) {
    if (mode == -1)
        cm.dispose();
    else {
        if (mode == 0) {
            cm.sendOk("下次再見.");
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 1)
            cm.sendNextPrev("#h #, 如果你給我 #b黑暗水晶#k 我將會讓你試著回答5個問題,若您5個問題都答對您將得到 #v4031058# #b智慧項鍊#k.");
        else if (status == 2) {
            if (!cm.haveItem(4005004)) {
                cm.sendOk("#h #, 你沒有 #b黑暗水晶#k");
                cm.dispose();
            } else {
                cm.gainItem(4005004, -1);
                cm.sendSimple("測驗開始 #b接受挑戰吧!#k.\r\n\r\n" + getQuestion(qChars[Math.floor(Math.random() * qChars.length)]));
                status = 2;
            }
        } else if (status == 3) {
            if (selection == correctAnswer)
                cm.sendOk("#h # 你答對了.\n準備答下一題??");
            else {
                cm.sendOk("你答錯了的答案!.\r\n很抱歉你必須在給我一個 #b黑暗水晶#k 才可以再挑戰!");
                cm.dispose();
            }
        } else if (status == 4)
            cm.sendSimple("測驗開始 #b接受挑戰吧!#k.\r\n\r\n" + getQuestion(qItems[Math.floor(Math.random() * qItems.length)]));
        else if (status == 5) {
            if (selection == correctAnswer)
                cm.sendOk("#h # 你答對了.\n準備答下一題??");
            else {
                cm.sendOk("你答錯了的答案!.\r\n很抱歉你必須在給我一個 #b黑暗水晶#k 才可以再挑戰!");
                cm.dispose();
            }
        } else if (status == 6) {
            cm.sendSimple("測驗開始 #b接受挑戰吧!#k.\r\n\r\n" + getQuestion(qMobs[Math.floor(Math.random() * qMobs.length)]));
            status = 6;
        } else if (status == 7) {
            if (selection == correctAnswer)
                cm.sendOk("#h # 你答對了.\n準備答下一題??");
            else {
                cm.sendOk("你答錯了的答案!.\r\n很抱歉你必須在給我一個 #b黑暗水晶#k 才可以再挑戰!");
                cm.dispose();
            }
        } else if (status == 8)
            cm.sendSimple("測驗開始 #b接受挑戰吧!#k.\r\n\r\n" + getQuestion(qQuests[Math.floor(Math.random() * qQuests.length)]));
        else if (status == 9) {
            if (selection == correctAnswer) {
                cm.sendOk("#h # 你答對了.\n準備答下一題??");
                status = 9;
            } else {
                cm.sendOk("你答錯了的答案!.\r\n很抱歉你必須在給我一個 #b黑暗水晶#k 才可以再挑戰!");
                cm.dispose();
            }
        } else if (status == 10) {
            cm.sendSimple("最後一個問題.\r\n測驗開始 #b接受挑戰吧!#k.\r\n\r\n" + getQuestion(qTowns[Math.floor(Math.random() * qTowns.length)]));
            status = 10;
        } else if (status == 11) {
            if (selection == correctAnswer) {
                cm.gainItem(4031058, 1);
                cm.warp(211000001, 0);
                cm.sendOk("恭喜 #h #, 你太強大了.\r\n拿著這個 #v4031058# 去找你的轉職教官吧!.");
                cm.dispose();
            } else {
                cm.sendOk("太可惜了,差一題就可以通關了!! 多多加油><.\r\n很抱歉你必須在給我一個 #b黑暗水晶#k 才可以再挑戰!");
                cm.dispose();
            }
        }
    }
}

function getQuestion(qSet) {
    var q = qSet.split("#");
    var qLine = q[0] + "\r\n\r\n#L0#" + q[1] + "#l\r\n#L1#" + q[2] + "#l\r\n#L2#" + q[3] + "#l\r\n#L3#" + q[4] + "#l";
    correctAnswer = parseInt(q[5], 10);
    correctAnswer--;
    return qLine;
}