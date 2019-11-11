package constants; 

/** 
 * NewYearCard 
 *  
 * @author Eric 
 */ 
public enum NewYearCardRes { 
    // NewYearCardReq 
    Send(0x0), 
    Receive(0x1), 
    Delete(0x2), 
    GetUnreceivedList(0x3), 
    // NewYearCardRes 
    Send_Done(0x4), 
    Send_Failed(0x5), 
    Receive_Done(0x6), 
    Receive_Failed(0x7), 
    Delete_Done(0x8), 
    Delete_Failed(0x9), 
    GetUnreceivedList_Done(0xA), 
    GetUnreceivedList_Failed(0xB), 
    NotiArrived(0xC), 
    BroadCast_AddCardInfo(0xD), 
    BroadCast_RemoveCardInfo(0xE), 
    // NewYearCardFailReason 
    CannotSendToSelf(0xF), 
    NoFreeSlot(0x10), 
    NotANewYearCard(0x11), 
    IncoherentItem(0x12), 
    TargetNotExist(0x13), 
    IncoherentData(0x14), 
    DBError(0x15), 
    UnknownError(0x16); 
    private final int result; 
     
    private NewYearCardRes(int result) { 
        this.result = result; 
    } 
     
    public int getResult() { 
        return result; 
    } 
}  