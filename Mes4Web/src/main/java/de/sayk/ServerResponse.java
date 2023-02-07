package de.sayk;

public class ServerResponse {
	public int returnCode = 0;
	public int outPara1 = 0;
	public int outPara2 = 0;

	@Override
	public String toString() {
		return "ServerResponse [returnCode=" + returnCode + ", outPara1=" + outPara1 + ", outPara2=" + outPara2 + "]";
	}

	public String toLog(String methodname, int clientId, int in1, int in2) {
		return methodname + "(" + clientId + ", " + in1 + ", " + in2 + ") => (" + returnCode + ", " + outPara1 + ", "
				+ outPara2 + ")";
	}
}
