package client;

import server.MapleStatEffect;
import server.life.Element;

/**
 * 
 * 廣泛型技能的介面
 */
public interface ISkill {

    /**
     * 取得技能ID
     * @return 
     */
    int getId();

    /**
     * 取得技能特效
     * @param skillLevel 技能等級
     * @return 技能特效資料
     */
    MapleStatEffect getEffect(int skillLevel);

    /**
     * 取得技能最大等級
     * @return 技能最大等級
     */
    byte getMaxLevel();

    /**
     * 回傳技能動畫時間
     * @return 技能動畫時間
     */
    int getAnimationTime();

    /**
     * 技能書使用的，用於學習技能書時，確認職業是否能學
     * @param job 角色職業
     * @return 是否能學習
     */
    public boolean canBeLearnedBy(int job);

    /**
     * 用於確認四轉職業的技能
     * @return 是否為四轉技能 
     */
    public boolean isFourthJob();

    /**
     * 回傳技能是否含有action值
     * @return 是否含有action值
     */
    public boolean hasAction();

    /**
     * 
     * @return 
     */
    public boolean isTimeLimited();

    public int getMasterLevel();

    public Element getElement();

    public boolean isBeginnerSkill();

    public boolean hasRequiredSkill();

    public boolean isInvisible();

    public boolean isChargeSkill();

    public int getRequiredSkillLevel();

    public int getRequiredSkillId();

    public String getName();
}
