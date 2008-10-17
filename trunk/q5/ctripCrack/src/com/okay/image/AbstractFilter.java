// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractFilter.java

package com.okay.image;


// Referenced classes of package com.okay.image:
//			Filter

public abstract class AbstractFilter
	implements Filter
{

	public AbstractFilter()
	{
	}

	public void doFilter(int data[][])
	{
		int h = data.length;
		if (h <= 0)
			return;
		int w = data[0].length;
		if (w <= 0)
			return;
		for (int i = 0; i < h; i++)
		{
			for (int j = 0; j < w; j++)
				data[i][j] = filter(data[i][j]);

		}

	}

	protected abstract int filter(int i);
}
