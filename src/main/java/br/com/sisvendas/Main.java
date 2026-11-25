package br.com.sisvendas;

import br.com.sisvendas.view.TelaPrincipal;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Ponto de entrada (Main) do Sistema de Informação SisVendas.
 * 
 * Disciplina: Programação Orientada a Objetos
 * Aluna: Iasmim José
 * 
 * @author Iasmim José
 */
public class Main {
    public static void main(String[] args) {
        // Configura aparência visual (Look & Feel)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
        }

        // Inicializa a interface gráfica na Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            TelaPrincipal principal = new TelaPrincipal();
            principal.setVisible(true);
        });
    }
}
