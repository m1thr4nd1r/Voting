package test;

import junit.framework.TestCase;

import org.junit.Test;

import agents.VoterAgent;
import behaviour.ListenBehaviour;

public class ListenBehaviourTest extends TestCase{

	@Test
	public void testFind() {
		ListenBehaviour l = new ListenBehaviour();
		VoterAgent v = new VoterAgent();
		String[] op = {"a","b","c","d","e"};
		
		v.setOptions(op);
		l.setAgent(v);
		
		assertEquals(0, l.find("a"));
		assertEquals(2, l.find("c"));
		assertEquals(4, l.find("e"));
		assertEquals(1, l.find("b"));
		assertEquals(3, l.find("d"));
		
		assertEquals(4, l.find("e"));
		assertEquals(2, l.find("c"));
		assertEquals(0, l.find("a"));
		
		assertEquals(3, l.find("e"));
		assertEquals(0, l.find("d"));
		assertEquals(0, l.find("d"));
	}

}