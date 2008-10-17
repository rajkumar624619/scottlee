// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   WhiteFilter.java

package com.okay.image;


// Referenced classes of package com.okay.image:
//			AbstractFilter

public class WhiteFilter extends AbstractFilter
{

	public WhiteFilter()
	{
	}

	protected int filter(int p)
	{
		return !isWhite(p) ? 0 : 1;
	}

	private boolean isWhite(int p)
	{
		return (p & 0xff) > 240 && (p >> 8 & 0xff) > 240 && (p >> 16 & 0xff) > 240;
	}
}
