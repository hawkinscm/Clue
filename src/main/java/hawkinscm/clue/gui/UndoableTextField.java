package hawkinscm.clue.gui;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class UndoableTextField extends JTextField {
	private static final long serialVersionUID = 1L;

	public UndoableTextField(int columns) {
		super(columns);
		init();
	}
	
	private void init() {
		Document document = getDocument();
		final UndoManager undoManager = new UndoManager();
		
		document.addUndoableEditListener(evt -> undoManager.addEdit(evt.getEdit()));

		getActionMap().put("Undo", new AbstractAction("Undo") {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent evt) {
				if (undoManager.canUndo())
					try { undoManager.undo(); } catch (CannotUndoException e) {}
			}
		});
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "Undo");

		getActionMap().put("Redo", new AbstractAction("Redo") {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent evt) {
				if (undoManager.canRedo())
					try { undoManager.redo(); } catch (CannotRedoException e) {}
			}
		});
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "Redo");
	}
}