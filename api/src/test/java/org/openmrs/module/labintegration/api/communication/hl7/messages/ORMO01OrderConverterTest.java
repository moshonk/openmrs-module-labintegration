package org.openmrs.module.labintegration.api.communication.hl7.messages;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.module.labintegration.api.communication.hl7.messages.testdata.HL7TestMsgUtil;
import org.openmrs.module.labintegration.api.communication.hl7.messages.testdata.HL7TestOrder;
import org.openmrs.module.labintegration.api.communication.hl7.messages.utils.OrderConverterTestUtils;
import org.openmrs.module.labintegration.api.hl7.messages.ORMO01OrderConverter;
import org.openmrs.module.labintegration.api.hl7.messages.OrderControl;
import org.openmrs.module.labintegration.api.hl7.messages.gnerators.MshGenerator;
import org.openmrs.module.labintegration.api.hl7.messages.gnerators.msh.MessageControlIdSource;
import org.openmrs.module.labintegration.api.hl7.scc.SCCHL7Config;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertEquals;

@ContextConfiguration(locations = { "classpath*:moduleApplicationContext.xml", "classpath*:applicationContext-service.xml",
        "classpath*:test-labContext.xml" }, inheritLocations = false)
public class ORMO01OrderConverterTest extends BaseModuleContextSensitiveTest {
	
	private static final String DATASET = "lab-dataset.xml";
	
	private static final String ORM_O01_FILE = "ORM_O01.hl7";
	
	private static final int PATIENT_ID = 10;
	
	@Spy
	@Autowired
	private MessageControlIdSource controlIdSource;
	
	@Autowired
	@InjectMocks
	private MshGenerator mshGenerator;
	
	@Autowired
	private ORMO01OrderConverter orderConverter;
	
	@Autowired
	private PatientService patientService;
	
	@Autowired
	private SCCHL7Config scchl7Config;
	
	@Before
	public void init() {
		OrderConverterTestUtils.mockRollingNumber(controlIdSource);
	}
	
	@Test
	public void shouldGenerateMessage() throws Exception {
		executeDataSet(DATASET);
		Patient patient = patientService.getPatient(PATIENT_ID);
		HL7TestOrder order = new HL7TestOrder(patient);
		
		String msg = orderConverter.createMessage(order.value(), OrderControl.NEW_ORDER, scchl7Config);
		
		String expected = HL7TestMsgUtil.readMsg(ORM_O01_FILE);
		assertEquals(expected, msg);
	}
}