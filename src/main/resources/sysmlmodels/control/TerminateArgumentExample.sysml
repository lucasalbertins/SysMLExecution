package TerminateActionsExample {
	private import ScalarValues::Boolean;
	
	action def InternalProcess;
	action def WorkflowProcess;
	
	part def Processor {
		ref action workflowProcess : WorkflowProcess;
		action internalProcess : InternalProcess {
			// ...
		}
	}

	action terminateProcessing {
		in processor : Processor;

		terminate processor.workflowProcess;
		terminate processor;
	}
}