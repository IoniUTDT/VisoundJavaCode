package StairCase;

import com.badlogic.gdx.utils.Array;

public class StairCase {
	
	/*
	% Demonstrates the use of the staircase algorithm
	% (c)2009 Arthur Lugtigheid (lugtigheid@gmail.com)
	% Last edit: 16 December 2009 - Version 2.0.3-beta
	%
	% This is the third beta version ready for testing. I welcome any comment
	% or critique. Please let me know if you find any bugs or have an idea for
	% additional functionality. There are some additions upcoming, but would
	% rather test the basic functionality first.
	%
	% Some of the staircase logic is loosely based on work in our lab by Dr. 
	% Andrew Welchman and Matthew Dexter of the University of Birmingham.
	*/
	
	
	/*
	% Set up some general stuff to determine when it should end: when it
	% reaches the maximum number of trials, or maximum number of reversals, the
	% termination rule is activated and the staircase ends. In the example
	% below, the first 6 reversals are ignored thus basing the analysis on the
	% last 10 reversals. 
	 */
	
	int maxTrials=100; //the maximum number of trials
	int reversals=16; //the maximum number of reversals
	int ignoreReversals=6; //number of reversals to ignore
	
	/*
	% Define the range in which the stimulus values are allowed to vary
	% (minstimval being the lower boundary and maxstimval being the upper
	% boundary). You can set the maximum amount that a staircase is allowed to
	% hit one of the boundaries by setting sc.maxboundaries - it will terminate
	% once the 'boundary hit counter' has reached this number.
	*/
	
	static int minstimval=0; //minimum stimulus value
	static int maxstimval=100; //maximum stimulus value
	static int maxboundaries=3; //number of times sc can hit boundary
	
	/*
	% Set up the behaviour of the staircase algorithm in terms of steps. It's
	% quite common to use fixed stepsizes as shown below, where the stepsize
	% decreases after each reversal. Another - new - option is to use random 
	% stepsizes. This chooses a random stepsize from the fixed stepsizes vector. 
	% The idea behind that is that this samples around threshold more broadly, 
	% enabling you to fit a psychometric function to your staircase data,
	% provided, of course, that you have enough data to do so.
	*/
	
	static STEPTYPE steptype = STEPTYPE.FIXED; // other option is 'random'
	float[] fixedstepsizes = {10, 5, 2.5f, 1}; //specifies the stepsize vector
	
	/*
	% TODO: fix the scaled stepsize mechanism - anything to do with the scaling
	% mechanism is currently (Jan 2010) a work in progress.
	*/
	
	int scalefactor = 5; //scale factor in percentages (N/A)
	
	/*
	% The code below defines the type of staircase. If your staircase is a 1
	% down / 3 up staircase, the proper way to set it up is as in this example.
	% This should (theoretically) converge to about 0.8 proportion - if you
	% want the 50% interval, you should use a 1 down / 1 up staircase.
	*/
	
	static int up = 1; //# of incorrect answers to go one step up
	static int down = 3;  //# of correct answers to go one step down
	static int condition=1;
	static int initial=0;
	
	Sim sim = new Sim(); // simulate responses
	Array<Stair> stairs = new Array<Stair> ();
	int trial; // % global trialcounter
	Stair activestair;
	
	public StairCase() {
		/*
		%% set up the staircases

		% The individual staircases are set up in an embedded structure called 
		% sc.stairs, and each one has an index. At the very least, it's necessary 
		% to initialise it with the initial stimulus value as shown below, but it 
		% should be possible to individually set up multiple variables in the future. 
		% Furthermore, it should be noted that the algoritm interleaves these
		% staircases randomly. This particular demo interleaves 4 staircases. 
		*/
		
		/*
		 % Please note that the visualise script uses above values, so if you change
		 % these values you need to change them in the visualisation script as well.

		 % You can set some extra's; for instance if we want the fourth staircase 
		 % to be a staircase that targets the 0.2 proportion so that we can take 
		 % the mean of the 0.2 and the 0.8; These values can be set anywhere before
		 % calling the init function (which sets the values):

		 % sc.stairs(4).up = 3; 
		 % sc.stairs(4).down = 1;
		*/

		/*
		% you can also link staircases by providing a condition id (if you don't 
		% specify this, it will just assume that all conditions are '1'). This 
		% doesn't do much now, but it will allow you to segregate your data later:
		*/
		
		Stair stair = new Stair();
		stair.initial = 0; 
		stairs.add(stair);

		stair = new Stair();
		stair.initial = 25; 
		stair.condition = 2;
		stairs.add(stair);
		
		stair = new Stair();
		stair.initial = 75;
		stair.steptype = STEPTYPE.RANDOM;// % or maybe we want the step type of staircase 3 set to random:
		stairs.add(stair);

		stair = new Stair();
		stair.initial = 100;
		stair.condition = 2;
		stairs.add(stair);
		
		/*
		% you can also add other (useful) variables to the struct that you might 
		% want to use later in your analysis by specifying them as follows:

		% global stair variable:         sc.<variable name>
		% stair specific variable:       sc.stairs(index).<variable name>
		*/
		
		/*
		% initialises the staircases

		% global trialcounter
		*/
		this.trial = 0;
		
		/*
		% get the number of staircases we want to initialise
		sc.num = numel(sc.stairs);

		% create a vector with the active staircases
		sc.active = 1:sc.num;

		% we're not done, we're only just starting!
		sc.done = 0;
		 */
		
		this.mainloop();
	}

	private void mainloop() {
		newtrial();
		
	}

	private void newtrial() {
		/*
		function [trial, sc] = newtrial(sc)
		% selects a random staircase and gets new trial parameters

		% select a random staircase from the active staircases - returns index of
		% sc rather than the index used in the active staircase vector
		sc.current = sc.active(ceil(numel(sc.active) * rand(1,1)));

		% increment the trial counter for the current staircase
		sc.stairs(sc.current).trial = sc.stairs(sc.current).trial + 1;

		% increment the total trial count
		sc.trial = sc.trial + 1;

		% sets a stimulus value
		sc = getstimval(sc);

		% set the values in the trial struct
		trial.stimval = sc.stairs(sc.current).stimval;
		trial.number = sc.stairs(sc.current).trial;
		*/
		
		// Select a random stair
		this.activestair = this.stairs.random();
		// Add counters
		this.trial++;
		this.activestair.trial++;
		
		
	}

	public class Stair {

		public int trial = 0; //% staircase specific trial number
		public int data[]; //contains raw data
		public int wrong = 0; // % number of correct answers
		public int right = 0; // % number of incorrect answers
		public int direction = 0; // the direction of the staircase
		public int reversal[]; // contains reversal data
		public int hitboundaries = 0; // counter for how often it hit the boundaries
		public int maxboundaries = StairCase.maxboundaries;
		public int up = StairCase.up;
		public int down = StairCase.down;
		public STEPTYPE steptype = StairCase.steptype;
		public int condition = StairCase.condition;
		public int minstimval = StairCase.minstimval;
		public int maxstimval = StairCase.maxstimval;
		public int initial = StairCase.initial;
		
	}
	
	public class Sim {
		/*
		% Set up the values used to simulate responses
		
		% PLEASE NOTE: due to the fact that we're using a 1-up/3-down staircase,
		% this will not target the threshold at 60, but rather the 0.8 proportion
		% (i.e. the area under the underlying gaussian) on the psychometric curve.
		% Also see above description.
		*/

		double mu = 50; //simulated threshold (mu)
		double sg = 15; //simulated sensitivity (sigma)
		
		/*
		% Changing the simulated threshold will shift the threshold - changing the
		% sensitivity will change the slope of the psychometric function, thereby
		% adding or reducing 'noise' in the responses.
		*/ 
	}
	public enum STEPTYPE {
		FIXED, RANDOM
	}
}
