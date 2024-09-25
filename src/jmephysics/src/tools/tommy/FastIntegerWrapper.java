package tools.tommy;

public final class FastIntegerWrapper implements Comparable
{

    private int value;

    public FastIntegerWrapper()
    {

    }
    
    public void setValue(int i)
    {
        value=i;
    }
    
    public int getValue()
    {
        return value;
    }

    public int hashCode()
    {
        return value;
    }

    public int compareTo(FastIntegerWrapper anotherInteger)
    {
        int thisVal = this.value;
        int anotherVal = anotherInteger.value;
        return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
    }

    public int compareTo(Object o)
    {
        return compareTo((FastIntegerWrapper) o);
    }

    public boolean equals(Object obj)
    {
        if (obj instanceof FastIntegerWrapper)
        {
            return value == ((FastIntegerWrapper) obj).value;
        }
        return false;
    }

    private static final long serialVersionUID = 1L;
}
