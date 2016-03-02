package gameLogic;

public interface Mountable<T> {
	public T getMounted();
	public boolean mount(T t);
	public boolean isMounted();
	public boolean canMount(Object object);
	public boolean unmount(Avatar avatar, Game game);
}
