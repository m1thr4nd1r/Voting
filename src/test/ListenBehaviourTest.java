package test;

import junit.framework.TestCase;

import org.junit.Test;

import agents.CreatorAgent;
import behaviour.ListenBehaviour;

public class ListenBehaviourTest extends TestCase{

	@Test
	public void testFind() {
		ListenBehaviour p = new ListenBehaviour();
		CreatorAgent c = new CreatorAgent();
		String[] op = {"a","b","c","d","e"};
		
		c.setOptions(op);
		p.setAgent(c);
		
		assertEquals(0, p.find("a"));
		assertEquals(2, p.find("c"));
		assertEquals(4, p.find("e"));
		assertEquals(1, p.find("b"));
		assertEquals(3, p.find("d"));
		
		assertEquals(4, p.find("e"));
		assertEquals(2, p.find("c"));
		assertEquals(0, p.find("a"));
		
		assertEquals(3, p.find("e"));
		assertEquals(0, p.find("d"));
		assertEquals(0, p.find("d"));
	}

}