//package br.ufrpe.dc.sysml.control;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import org.junit.jupiter.api.Test;
//
//class IFlowTest {
//
//    @Test
//    void testSimpleFlowWithoutPayload() {
//        IFlow adapter = createFlowAdapterFromModel("SimpleFlow.sysml");
//
//        assertEquals("trigger.scene", adapter.getSource());
//        assertEquals("focus.scene", adapter.getTarget());
//        assertNull(adapter.getPayload());
//        assertEquals("flow from trigger.scene to focus.scene", adapter.toString());
//    }
//
//    @Test
//    void testFlowWithPayload() {
//    	IFlow adapter = createFlowAdapterFromModel("FlowWithPayload.sysml");
//
//        assertEquals("tankAssy.fuelTankPort.fuelSupply", adapter.getSource());
//        assertEquals("eng.engineFuelPort.fuelSupply", adapter.getTarget());
//        assertEquals("Fuel", adapter.getPayload());
//        assertEquals(
//            "flow of Fuel from tankAssy.fuelTankPort.fuelSupply to eng.engineFuelPort.fuelSupply",
//            adapter.toString()
//        );
//    }
//
//    /** Método auxiliar para carregar um modelo e criar o adaptador */
//    private FlowAdapter createFlowAdapterFromModel(String fileName) {
//        // Aqui, no teste, você pode chamar seu loader SysML para achar o FlowUsage e criar o adapter
//        throw new UnsupportedOperationException("Ainda não implementado");
//    }
//
//}
package br.ufrpe.dc.sysml.control;


