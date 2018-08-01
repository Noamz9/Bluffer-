package main.java.bgu.spl.general;

/**
 * object question 
 *String questionText- the question from the Json
 *String realAnswer- the real answer
 */
public class question {

	private String questionText;
	private String realAnswer;
	
	/**
	 * constractor
	 */
	public question(String questionText,String realAnswer){
		this.realAnswer=realAnswer;
		this.questionText=questionText;
	}
	/**
	 * @return String getRealAns- the real ans
	 */
	public String getRealAns(){
		return realAnswer;
	}
	/**
	 * @return the question
	 */
	public String getQuestion(){
		return questionText;
	}

}
