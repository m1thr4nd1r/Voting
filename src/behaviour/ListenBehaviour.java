package behaviour;

import java.util.Random;

import agents.VoterAgent;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class ListenBehaviour extends SimpleBehaviour
{
	private VoterAgent agent;
	private boolean done;
	
	public ListenBehaviour(Agent a)
	{
		this.myAgent = a;
		this.agent = (VoterAgent) a;
		this.done = false;
	}
	
	public ListenBehaviour(){}

	public void setAgent(Agent a)
	{
		this.agent = (VoterAgent) a;
	}
	
	public void action()
	{
		ACLMessage msg = myAgent.receive();
		
		while (msg != null && msg.getConversationId() != null)
		{
			if (msg.getConversationId().equals("Start"))
			{
				if (msg.getContent().equals("Simple"))
				{
//					System.out.println("Cheguei a ler a mensagem Simple (" + extractNumber(myAgent.getName()) + ")");
					ACLMessage reply = msg.createReply();
					reply.setContent(simpleVoting());
					this.myAgent.send(reply);
				}
			}
			else if (msg.getConversationId().equals("Done"))
				this.done = true;
			
			msg = myAgent.receive();
		}		
	}
	
	public int find(String vote)
	{
		int i = 0;
		
		while (!agent.getOption(i).equals(vote))
			i++;
		
		return i;
	}
	
	private String simpleVoting()
	{
//		System.out.println("Cheguei no simpleVoting (" + extractNumber(myAgent.getName()) + ")");
		String choice = shuffle(agent.getOptions())[0];
		
		return String.valueOf(find(choice));
	}
	
	private String[] shuffle(String[] options)
	{
		int i,j;
//		System.out.println("Cheguei no shuffle (" + extractNumber(myAgent.getName()) + ")");
		Random generator = new Random();
		
		for (i = options.length - 1; i > 0; i--)
		{
			j = generator.nextInt(i);
			String temp = options[i];
			options[i] = options[j];
			options[j] = temp;
		}
		print(options);
		
		return options;
	}
	
	private void print(String[] options)
	{
		int i;
		String text;
		
		text = "Ordem de prioridade das opções do eleitor " + extractNumber(myAgent.getName()) + " : ";
		for (i = 0; i < options.length; i++)
			text+= options[i] + " ";
		
		System.out.println(text);
	}
	
	public String extractNumber(String s)
	{
		int end = s.indexOf('@',0);
		int begin = s.lastIndexOf('t', end);
		
		return s.substring(begin + 1, end);
	}
	
	public int onEnd()
	{
		myAgent.doDelete();
		return 0;
	}

	@Override
	public boolean done() {
		return done;
	}
}