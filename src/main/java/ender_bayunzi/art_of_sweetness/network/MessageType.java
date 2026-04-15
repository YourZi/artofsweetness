package ender_bayunzi.art_of_sweetness.network;

import java.util.concurrent.Callable;

public enum MessageType {
	keyUp(() -> new MessageKeyUP()),
	keyDown(() -> new MessageKeyDown()),
	syncMagicList(() -> new MessageSyncMagicList()),
	overlayRenderX(() -> new MessageOverlayRenderX());
	
	public final Callable<IFMessage> callable;
	
	MessageType(Callable<IFMessage> callable) {
		this.callable = callable;
	}
	
}
