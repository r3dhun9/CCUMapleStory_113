
/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

status = -1;
var sel, sel2;

function start() {
    cm.sendSimple("您號，楓葉市場 今天開幕~.#b\r\n#L0#讓我去楓葉市場\r\n#L1#聽有關楓葉市場的解釋");
}

function action(mode, type, selection) {
    status++;
    if (status == 6 && mode == 1) {
        sel2 = undefined;
        status = 0;
    }
    if (mode != 1) {
        if (mode == 0 && type == 0)
            status -= 2;
        else {
            cm.dispose();
            return;
        }
    }
    if (status == 0) {
        if (sel == undefined)
            sel = selection;
        if (selection == 0) {
            cm.sendNext("好了，我將送你到楓葉市場地圖.");
        } else
            cm.sendSimple("請問您想了解楓葉市場哪部分??#b\r\n#L0#那是什麼地方??\r\n#L1#能在楓葉市場幹什麼事情??\r\n#L2#我沒有任何問題");
    } else if (status == 1) {
        if (sel == 0) {
            cm.saveLocation("EVENT");
            cm.warp(680100000 + parseInt(Math.random() * 3));
            cm.dispose();
        } else if (selection == 0) {
            cm.sendNext("楓葉市場只有在假日開放。如果您在其他城鎮發現我也可以在那找到我進入, 我幾乎無所不在!!!");
            status -= 2;
        } else if (selection == 1)
            cm.sendSimple("你可以在楓葉市場找到其他地方很難找到的罕見商品.#b\r\n#L0#購買特殊物品\r\n#L1#幫助家禽農場業主");
        else {
            cm.sendNext("我猜你沒有任何問題，假如你好奇請讓我猜你的想法並詢問你煩惱什麼~");
            cm.dispose();
        }
    } else if (status == 2) {
        if (sel2 == undefined)
            sel2 = selection;
        if (sel2 == 0)
            cm.sendNext("你可以找到許多道具在楓葉市場,價格很容易有變動,所以你最好在他改變價格前往採買,因為他們變動的時候很便宜的!!");
        else
            cm.sendNext("除了商人你還可以找到養雞場煮人在楓葉市場裡面,幫助咪咪和她孵化的蛋,直到雞長大變成一隻好吃的雞!");
    } else if (status == 3) {
        if (sel2 == 0)
            cm.sendNextPrev("在這裡進行的購買可以賣回給商家的中介，阿杜蘭。他不會接受任何超過一個星期的時候，所以在週六確保你再賣！");
        else
            cm.sendNextPrev("由於她不能只相信任何人的雞蛋，她會問保證金。支付她的存款，並採取卵子照顧好.");
    } else if (status == 4) {
        if (sel2 == 0)
            cm.sendNextPrev("阿度蘭調整他的倒賣率一樣，所以這將是明智的，賣的時候就可以使最大的利潤。價格往往波動小時，所以記得要經常檢查.");
        else
            cm.sendNextPrev("如果您管理蛋成功成長為雞拿回咪咪，咪咪會報答你。她可能是懶惰的，但她不領情.");
    } else if (status == 5) {
        if (sel2 == 0)
            cm.sendNextPrev("通過購買善於在楓葉市場價格出售給商家的中介時，其價值上升測試你的業務的機智！");
        else
            cm.sendNextPrev("您可以在雞蛋點擊查看關於它的增長。你必須用勤奮，因為你獲得和雞蛋一起成長的EXP雞蛋。");
    }
}