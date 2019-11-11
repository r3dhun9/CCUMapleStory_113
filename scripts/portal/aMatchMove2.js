function enter(pi) { 
	var returnMap = pi.getSavedLocation("ARIANT"); 
 	if (returnMap < 0) { 
		returnMap = 100000000;
 	} 
 	pi.playPortalSE(); 
 	pi.clearSavedLocation("ARIANT"); 
 	pi.warp(returnMap, 0); 
 	return true;
}  