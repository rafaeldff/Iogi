package iogi;



public interface Instantiator<T> {
	public boolean isAbleToInstantiate(Target<?> target);
	public T instantiate(Target<?> target, Parameters parameters);
}
