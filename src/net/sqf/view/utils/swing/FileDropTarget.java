package net.sqf.view.utils.swing;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public abstract class FileDropTarget implements DragGestureListener,
		DropTargetListener {
	
	public FileDropTarget(JPanel panel) {
		new DropTarget(panel, this);
		DragSource dragSource = DragSource.getDefaultDragSource();
		 dragSource.createDefaultDragGestureRecognizer(panel, DnDConstants.ACTION_COPY, this);
	}
	
//	public abstract File[] getDragedFileList();

	
	@Override
	public void drop(DropTargetDropEvent dte) {
		dte.acceptDrop(DnDConstants.ACTION_COPY);
		
		// Get the transfer which can provide the dropped item data
		Transferable transferable = dte.getTransferable();

		// Get the data formats of the dropped item
		DataFlavor[] flavors = transferable.getTransferDataFlavors();
		
		for (DataFlavor flavor : flavors) {

			try {

				// If the drop items are files
				if (flavor.isFlavorJavaFileListType()) {

					// Get all of the dropped files
					List<File> files = (List<File>) transferable
							.getTransferData(flavor);

					// Loop them through
					this.receiveFiles(files);

				}

			} catch (Exception e) {

				// Print out the error stack
				e.printStackTrace();

			}
		}

		// Inform that the drop is complete
		dte.dropComplete(true);

	}


	public abstract void receiveFiles(List<File> files);

	@Override
	public void dragEnter(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragExit(DropTargetEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragOver(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public void dropActionChanged(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragGestureRecognized(DragGestureEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
