package br.ufrpe.dc.sysml.control;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({ ActionDefinitionAdapterTest.class, ControlNodeAdapterTest.class, DecisionControlTest.class,
		ExploreUUIDTest.class, FlowAdapterFlowUsageTest.class,
		FlowAdapterMergeExampleTest.class, FlowUsageAdapterTest.class, JoinNodeTest.class, NodeAdapterTest.class })
public class ControlNodeTests {

}