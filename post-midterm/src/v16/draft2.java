package v16;


import java.util.concurrent.Callable;

class draft2 implements Callable<Void> {

	String string;



	public draft2(String string) {
		this.string = string;
	}

	public Void call() {
			printString();
			return null;
	}

	private void printString() {
		System.out.println(string);
		   try {  Thread.sleep(5000);  } catch (InterruptedException e) { e.printStackTrace(); }  
		
	}


	
}