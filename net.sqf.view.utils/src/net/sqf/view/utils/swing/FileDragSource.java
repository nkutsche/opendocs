package net.sqf.view.utils.swing;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public abstract class FileDragSource implements DragGestureListener,
		DragSourceListener {
	
	public FileDragSource(JPanel panel){
		DragSource dragSource = DragSource.getDefaultDragSource();
		 dragSource.createDefaultDragGestureRecognizer(panel, DnDConstants.ACTION_COPY, this);
	}
	
	
	public abstract File[] getDragedFileList();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	class FileListTransferable implements Transferable {
		public FileListTransferable(File[] files) {
			fileList = new ArrayList();
			for (int i = 0; i < files.length; i++) {
				fileList.add(files[i]);
			}
		}

		// Implementation of the Transferable interface
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { DataFlavor.javaFileListFlavor };
		}

		public boolean isDataFlavorSupported(DataFlavor fl) {
			return fl.equals(DataFlavor.javaFileListFlavor);
		}

		public Object getTransferData(DataFlavor fl) {
			if (!isDataFlavorSupported(fl)) {
				return null;
			}

			return fileList;
		}

		List fileList; // The list of files
	}

	@Override
	public void dragGestureRecognized(DragGestureEvent dge) {
		File[] fileList = getDragedFileList();
		if(fileList.length > 0){
			Transferable transferable = new FileListTransferable(getDragedFileList());
			dge.startDrag(null, transferable, this);
		}
	}

	@Override
	public void dragDropEnd(DragSourceDropEvent arg0) {
	}

	@Override
	public void dragEnter(DragSourceDragEvent arg0) {
	}

	@Override
	public void dragExit(DragSourceEvent arg0) {
	}

	@Override
	public void dragOver(DragSourceDragEvent arg0) {
	}
	
	@Override
	public void dropActionChanged(DragSourceDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
