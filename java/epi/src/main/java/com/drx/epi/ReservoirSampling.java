// Copyright (c) 2013 Elements of Programming Interviews. All rights reserved.
package com.drx.epi;

//import static com.drx.epi.utils.Utils.simplePrint;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReservoirSampling {
	// @include
	static List<Integer> reservoir_sampling(InputStream sin, int k)
			throws IOException, ClassNotFoundException {
		
		List<Integer> R = new ArrayList<Integer>();
		
		ObjectInputStream osin = new ObjectInputStream(sin);
		// Store the first k elements.
		Integer x = (Integer) readObjectSilently(osin);
		for (int i = 0; i < k && x != null; ++i) {
			R.add(x);
			x = (Integer) readObjectSilently(osin);
		}

		// After the first k elements.
		int element_num = k + 1;
		x = (Integer) readObjectSilently(osin);
		while (x != null) {
			Random gen = new Random(); // random num generator.
			// Generate random int in [0, element_num].
			int tar = gen.nextInt(++element_num);
			if (tar < k) {
				R.set(tar, x);
			}
			
			x = (Integer) readObjectSilently(osin);
		}
		
		close(osin);
		return R;
	}
	
	private static Object readObjectSilently(ObjectInputStream osin)
			throws ClassNotFoundException, IOException {
		Object object = null;
		try {
			object = osin.readObject();
		} catch (EOFException e) {
			// we don't want to force the calling code to catch an EOFException
			// only to realize there is nothing more to read.
		}
		return object;
	}

	private static void close(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException e) {
			// We want to close "closeable" silently
		}
	}
	
	// @exclude

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		int n, k;
		Random gen = new Random();
		
		if (args.length == 1) {
			n = Integer.valueOf(args[0]);
			k = gen.nextInt(n) + 1;
		} else if (args.length == 2) {
			n = Integer.valueOf(args[0]);
			k = Integer.valueOf(args[1]);
		} else {
			n = gen.nextInt(100000);
			k = gen.nextInt(n) + 1;
		}
		
		List<Integer> A = new ArrayList<Integer>(n);
		for (int i = 0; i < n; ++i) {
			A.add(i);
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		for (Integer i : A) {
			oos.writeObject(i);
		}
		
		System.out.println(n + " " + k);
		
		ByteArrayInputStream sin = new ByteArrayInputStream(baos.toByteArray());
		List<Integer> ans = reservoir_sampling(sin, k);
		
		assert ans.size() == k;

		close(baos);
		close(oos);
		// simplePrint(ans);
	}
	
}
