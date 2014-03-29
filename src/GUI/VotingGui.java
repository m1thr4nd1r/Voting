package GUI;

import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JComboBox;

import java.awt.GridBagLayout;

import javax.swing.JButton;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingConstants;
import javax.swing.DefaultComboBoxModel;

import java.awt.Font;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;

@SuppressWarnings("serial")
public class VotingGui extends JFrame {

	private JPanel contentPane;
	private List<JTextField> options;
	private JTextField txtVotingType;
	private JTextField txtAgentQnt;
	private JTextField agentQnt;
	private JComboBox<String> comboBox;
	private JButton btnStart;
	private JTextField txtAgentesComFalha;
	private JCheckBox chckbxExisteFalha;
	private JTextField buggyAgents;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VotingGui frame = new VotingGui();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public VotingGui() {
		setBounds(100, 100, 550, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		options = new ArrayList<JTextField>();		
		
		txtVotingType = new JTextField();
		txtVotingType.setEditable(false);
		txtVotingType.setHorizontalAlignment(SwingConstants.CENTER);
		txtVotingType.setText("Escolha o tipo de vota\u00E7\u00E3o: ");
		GridBagConstraints gbc_txtTipoDeVotao = new GridBagConstraints();
		gbc_txtTipoDeVotao.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtTipoDeVotao.insets = new Insets(0, 0, 20, 5);
		gbc_txtTipoDeVotao.gridx = 0;
		gbc_txtTipoDeVotao.gridy = 1;
		contentPane.add(txtVotingType, gbc_txtTipoDeVotao);
		txtVotingType.setColumns(10);
		
		JButton btnNewOpcao = new JButton("Adicionar Op\u00E7\u00E3o");
		GridBagConstraints gbc_btnNewOpcao = new GridBagConstraints();
		gbc_btnNewOpcao.insets = new Insets(0, 0, 20, 0);
		gbc_btnNewOpcao.gridx = 1;
		gbc_btnNewOpcao.gridy = 1;
		contentPane.add(btnNewOpcao, gbc_btnNewOpcao);
		
		btnNewOpcao.addActionListener(new Opcao(1,contentPane,options));
		
		comboBox = new JComboBox<String>();
		comboBox.setToolTipText("Selecione o tipo de vota\u00E7\u00E3o que deseja");
		comboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"Pluralidade", "Borda", "Sequencial"}));
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.insets = new Insets(0, 0, 20, 5);
		gbc_comboBox.gridx = 0;
		gbc_comboBox.gridy = 2;
		contentPane.add(comboBox, gbc_comboBox);
		
		txtAgentQnt = new JTextField();
		txtAgentQnt.setHorizontalAlignment(SwingConstants.CENTER);
		txtAgentQnt.setEditable(false);
		txtAgentQnt.setText("Quantidade de Agentes:");
		GridBagConstraints gbc_txtQuantidadeDeAgentes = new GridBagConstraints();
		gbc_txtQuantidadeDeAgentes.insets = new Insets(0, 0, 20, 5);
		gbc_txtQuantidadeDeAgentes.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtQuantidadeDeAgentes.gridx = 0;
		gbc_txtQuantidadeDeAgentes.gridy = 3;
		contentPane.add(txtAgentQnt, gbc_txtQuantidadeDeAgentes);
		txtAgentQnt.setColumns(10);
		
		agentQnt = new JTextField();
		agentQnt.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_agentQnt = new GridBagConstraints();
		gbc_agentQnt.insets = new Insets(0, 0, 20, 5);
		gbc_agentQnt.gridx = 0;
		gbc_agentQnt.gridy = 4;
		contentPane.add(agentQnt, gbc_agentQnt);
		agentQnt.setColumns(10);
		
		chckbxExisteFalha = new JCheckBox("Existe falha na vota\u00E7\u00E3o ?");
		GridBagConstraints gbc_chckbxExisteFalha = new GridBagConstraints();
		gbc_chckbxExisteFalha.insets = new Insets(0, 0, 20, 5);
		gbc_chckbxExisteFalha.gridx = 0;
		gbc_chckbxExisteFalha.gridy = 5;
		contentPane.add(chckbxExisteFalha, gbc_chckbxExisteFalha);
		
		txtAgentesComFalha = new JTextField();
		txtAgentesComFalha.setText("Agentes com falha (separados por virgula):");
		txtAgentesComFalha.setHorizontalAlignment(SwingConstants.CENTER);
		txtAgentesComFalha.setEditable(false);
		txtAgentesComFalha.setColumns(10);
		GridBagConstraints gbc_txtAgentesComFalha = new GridBagConstraints();
		gbc_txtAgentesComFalha.insets = new Insets(0, 0, 20, 5);
		gbc_txtAgentesComFalha.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtAgentesComFalha.gridx = 0;
		gbc_txtAgentesComFalha.gridy = 6;
		contentPane.add(txtAgentesComFalha, gbc_txtAgentesComFalha);
		
		buggyAgents = new JTextField();
		GridBagConstraints gbc_buggyAgents = new GridBagConstraints();
		gbc_buggyAgents.insets = new Insets(0, 0, 20, 5);
		gbc_buggyAgents.gridx = 0;
		gbc_buggyAgents.gridy = 7;
		contentPane.add(buggyAgents, gbc_buggyAgents);
		buggyAgents.setColumns(10);
		
		btnStart = new JButton("Start");
		btnStart.setForeground(new Color(0, 0, 0));
		btnStart.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_btnStart = new GridBagConstraints();
		gbc_btnStart.insets = new Insets(0, 0, 0, 5);
		gbc_btnStart.gridx = 0;
		gbc_btnStart.gridy = 8;
		contentPane.add(btnStart, gbc_btnStart);
		
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				String[] bugAgents = getBuggyAgents().split(",");
				String[] voterOptions = new String[options.size()+bugAgents.length+4];				
				
				String t = getVotingType();
				
				if (t.equals("Pluralidade"))
					voterOptions[0] = "Plurality";
				else if (t.equals("Sequencial"))
					voterOptions[0] = "Sequential";
				else
					voterOptions[0] = t;
				
				voterOptions[1] = String.valueOf(getAgentQnt());
				
				voterOptions[2] = String.valueOf(options.size());
				
				for (int i = 0; i < options.size(); i++)
					voterOptions[i+3] = options.get(i).getText();
				
				voterOptions[options.size() + 3] = String.valueOf(getFalha());
				
				for (int i = 0; i < bugAgents.length; i++)
					voterOptions[options.size() + 4] = bugAgents[i];
				
				Runtime rt = Runtime.instance();
				Profile p = new ProfileImpl();
				ContainerController agentContainer = rt.createAgentContainer(p);
				
				try {
					AgentController ac = agentContainer.createNewAgent("CreatorAgent", "agents.CreatorAgent", voterOptions);
					ac.start();
				} catch (StaleProxyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public String getVotingType() {
		return (String) comboBox.getSelectedItem();
	}
	public int getAgentQnt() {
		return Integer.valueOf(agentQnt.getText());
	}
	public String getFalha() {
		return String.valueOf(chckbxExisteFalha.isSelected());
	}
	public String getBuggyAgents() {
		return buggyAgents.getText();
	}
}

class Opcao implements ActionListener
{
	private int previousY;
	private JPanel contentPane;
	private List<JTextField> fields;
	
	Opcao(int previousY, JPanel panel, List<JTextField> f){
        super();
        this.previousY = previousY;
        this.contentPane = panel;
        this.fields = f;
    }
    
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// create the new text field
		if (previousY < 7)
		{
			previousY+=1;
				
			JTextField opcao = new JTextField();
			opcao.setHorizontalAlignment(SwingConstants.CENTER);
		    GridBagConstraints gbc_txtOpcao = new GridBagConstraints();
			gbc_txtOpcao.insets = new Insets(0, 0, 20, 5);
			gbc_txtOpcao.gridx = 1;
			gbc_txtOpcao.gridy = previousY;
			contentPane.add(opcao, gbc_txtOpcao);
			opcao.setColumns(10);
		    fields.add(opcao);		
			
		    contentPane.validate();
		    contentPane.repaint();		
		}
	}
}