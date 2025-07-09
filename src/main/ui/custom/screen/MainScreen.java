package main.ui.custom.screen;

import main.model.Space;
import main.service.BoardService;
import main.service.EventEnum;
import main.service.NotifierService;
import main.ui.custom.button.CheckGameStatusButton;
import main.ui.custom.button.FinishGameButton;
import main.ui.custom.button.ResetButton;
import main.ui.custom.frame.MainFrame;
import main.ui.custom.input.NumberText;
import main.ui.custom.panel.MainPanel;
import main.ui.custom.panel.SudokuSector;

import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

import static javax.swing.JOptionPane.*;
import static main.service.EventEnum.CLEAR_SPACE;

public class MainScreen {

    private final static Dimension dimension = new Dimension(600,600);

    private final NotifierService notifierService;

    private final BoardService boardService;

    public MainScreen(final Map<String, String> gameConfig) {
        this.boardService = new BoardService(gameConfig);
        this.notifierService = new NotifierService();
    }

    public void buildMainScreen() {
        JPanel mainPanel = new MainPanel(dimension);
        JFrame mainFrame = new MainFrame(dimension, mainPanel);
        for (int l = 0; l < 9; l+=3) {
            var endLin = l + 2;
            for (int c = 0; c < 9; c+=3) {
                var endCol = c + 2;
                var spaces = getSpacesFromSector(boardService.getSpaces(), c, endCol, l, endLin);
                JPanel sector = generateSection(spaces);
                mainPanel.add(sector);
            }
        }
        addResetButton(mainPanel);
        addCheckGameStatusButton(mainPanel);
        addFinishGameButton(mainPanel);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    private List<Space> getSpacesFromSector(final List<List<Space>> spaces
            , final int initCol
            , final int endCol
            , final int initLin
            , final int endLin) {
        List<Space> spaceSector = new ArrayList<>();
        for (int l = initLin; l <= endLin; l++) {
            for (int c = initCol; c <= endCol; c++) {
                spaceSector.add(spaces.get(c).get(l));
            }
        }
        return spaceSector;
    }

    private JPanel generateSection(final List<Space> spaces) {
        List<NumberText> fields = new ArrayList<>(spaces.stream().map(NumberText::new).toList());
        fields.forEach(t -> notifierService.subscribe(CLEAR_SPACE, t));
        return new SudokuSector(fields);
    }

    private JButton resetbutton;
    private JButton checkGameStatusButton;
    private JButton finishGameButton;

    private void addResetButton(JPanel mainPanel) {
        resetbutton = new ResetButton(e -> {
            var dialogResult = showConfirmDialog(
                    null,
                    "Deseja relamente reiniciar o jogo?",
                    "Limpar o jogo",
                    YES_NO_OPTION,
                    QUESTION_MESSAGE
            );
            if (dialogResult == 0) {
                boardService.reset();
                notifierService.notify(CLEAR_SPACE);
            }
        });
        mainPanel.add(resetbutton);
    }

    private void addCheckGameStatusButton(JPanel mainPanel) {
        checkGameStatusButton = new CheckGameStatusButton(e -> {
            var hasErros = boardService.hasError();
            var gameStatus = boardService.getStatus();
            var message = switch (gameStatus) {
                case NON_STARTED -> "O jogo não foi iniciado";
                case INCOMPLETE -> "O jogo está imcompleto";
                case COMPLETE -> "O jogo está completo";
            };
            message += hasErros ? " e contém erros." : " e não contém erros.";
            showMessageDialog(null,message);
        });
        mainPanel.add(checkGameStatusButton);
    }

    private void addFinishGameButton(JPanel mainPanel) {
        finishGameButton = new FinishGameButton(e -> {
           if (boardService.gameIsFinished()) {
               showMessageDialog(null,
                       "Parabéns, você cncluiu o jogo");
               resetbutton.setEnabled(false);
               checkGameStatusButton.setEnabled(false);
               finishGameButton.setEnabled(false);
           } else {
               showMessageDialog(null,
                       "Seu jogo tem alguma inconcistência, ajuste e tente novamente");
           }
        });
        mainPanel.add(finishGameButton);
    }
}
