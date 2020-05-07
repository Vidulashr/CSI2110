
//NAME: Vidulash Rajaratnam
//NUMBER: 8190398

import java.net.InetAddress;

/**
 * Array Heap implimentation of a priority queue
 * @author Lachlan Plant
 */
public class HeapPriorityQueue <K extends Comparable, V> implements PriorityQueue <K, V> {

	private Entry[] minHeap; //The minHeap itself in array form
	private int minTail;    //Index of last element in the minheap
	private Entry[] maxHeap; //The reference maxHeap in array form
	private int maxTail; //Index of last element in the reference maxheap
	private Entry<K,V> buffer; //The buffer


	/**
	 * Default constructor
	 */
	public HeapPriorityQueue() {
		this(100);
	}


	/**
	 * HeapPriorityQueue constructor with max storage of size elements
	 */
	public HeapPriorityQueue( int size ) {
		minHeap = new Entry [ size ]; //initialize minHeap with size number of elements
		maxHeap = new Entry [ size ]; //initialize maxHeap with size number of elements
		buffer = null; //initialize empty buffer
		maxTail = -1; //initialize tail of maxHeap
		minTail = -1; //initialize tail of minHeap
	}


	/****************************************************
	*
	*             Priority Queue Methods
	*
	****************************************************/

	/**
	 * Returns the number of items in the priority queue.
	 * O(1)
	 * @return number of items
	 */
	public int size() {
		//Gets size of minHeap,maxHeap and buffer individually
		if (buffer==null){ //if buffer empty, does not include it
			return (minTail + 1)+(maxTail + 1);} //add them together to get total items in priority queue
		return (minTail + 1)+(maxTail + 1)+1; //if buffer occupied, includes it
	} /* size */


	/**
	 * Tests whether the priority queue is empty.
	 * O(1)
	 * @return true if the priority queue is empty, false otherwise
	 */
	public boolean isEmpty() {
		if (buffer==null){ //if buffer is empty
			return minTail < 0;} //Check if minHeap is empty, should be same for maxHeap
		return false; //if buffer is not empty, never empty
	} /* isEmpty */


	/**
	 * Inserts a key-value pair and returns the entry created.
	 * O(log(n))
	 * @param key     the key of the new entry
	 * @param value   the associated value of the new entry
	 * @return the entry storing the new key-value pair
	 * @throws IllegalArgumentException if the heap is full
	 */
	public Entry <K, V>insert( K key, V value) throws IllegalArgumentException {
		if( minTail == minHeap.length - 1 ) //if heaps are full, return error message
			throw new IllegalArgumentException ( "Heap Overflow" );

		//Create new Entry element using inputted values
		Entry <K, V>    e = new Entry <> ( key, value );

		//If buffer is EMPTY
		if (buffer == null){
			buffer = e; //set e as buffer
			buffer.setIndex(-1);
			return e; //returns e right away
		}

		//If buffer is NOT empty
		buffer.setAssociate(e); //associate buffer with new element
		e.setAssociate(buffer); //associate new element with buffer

		//Compares current buffer value to new Entry e value
		if (buffer.getKey().compareTo(e.getKey()) > 0 ){ //If buffer larger
			minHeap[++minTail] = e; //add Entry e in minHeap
			e.setIndex(minTail);
			upHeapMin(minTail);

			Entry <K,V> temp = buffer; //create temp value with buffer value
			buffer = null; //clear buffer
			maxHeap[++maxTail] = temp; //add temp value to maxHeap
			temp.setIndex(maxTail); //set index of new value
			upHeapMax(maxTail); //upheap inserted value
			return e; //returns entry
		}
		//If buffer smaller
		maxHeap[++maxTail] = e; //add Entry e in maxHeap
		e.setIndex(maxTail);
		upHeapMax(maxTail);

		Entry<K,V> temp = buffer; //create temp value with buffer value
		buffer = null; //clear buffer
		minHeap[++minTail] = temp; //add temp value to minHeap
		temp.setIndex(minTail);
		upHeapMin(minTail);
		return e;
	} /* insert */


	/**
	 * Returns (but does not remove) an entry with minimal key.
	 * O(1)
	 * @return entry having a minimal key (or null if empty)
	 */
	public Entry <K, V> min() {
		if( isEmpty() ) //If heaps are empty, returns null
			return null;
		Entry minkey = minHeap[0]; //Sets root of minHeap as minimal key as default
		//If buffer is empty, return max
		if (buffer==null){
			return minkey; //return minimal key
		}
		//If buffer NOT empty
		//Compares value of root of minHeap to the buffer
		if ((Integer) minHeap[0].getKey()> (Integer) buffer.getKey()){ //Converts to double to compare values
			minkey = buffer; //If root is larger than buffer, sets buffer as minimal key
		}
		return minkey; //Returns minimal key
	} /* min */

	//Below is a added method for assignment 2 that was not previously implemented
	/**
	 * Returns (but does not remove) an entry with maximum key.
	 * O(1)
	 * @return entry having a maximum key (or null if empty)
	 */
	public Entry <K, V> max() {
		if( isEmpty() ) //If heaps are empty, return null
			return null;
		Entry maxkey = maxHeap [ 0 ]; //Sets root of maxHeap as maximum key as default
		//If buffer is empty, return max
		if (buffer==null){
			return maxkey; //return maximum key
		}
		//If buffer NOT empty
		//Compares value of root of maxHeap to the buffer
		if ((Integer) maxHeap[0].getKey()<(Integer) buffer.getKey()){
			maxkey = buffer; //If root is smaller than buffer, sets buffer as maximum key
		}
		return maxkey; //Returns maximum key
	} /* max */


	/**
	 * Removes and returns an entry with minimal key.
	 * O(log(n))
	 * @return the removed entry (or null if empty)
	 */
	public Entry <K, V> removeMin() {
		if( isEmpty() ) //If queue is empty, return null
			return null;

		Entry ret = minHeap[ 0 ]; //save min value in minHeap, ROOT of minHeap
		Entry ret_ref = ret.getAssociate(); //save associated value of minHeap ROOT from maxHeap
		int associate_index = ret_ref.getIndex(); //save index of associated value

		//If buffer is EMPTY, doesn't need to compare with ROOT
		if (buffer == null){
			//REMOVING ASSOCIATE AND RE-ESTABLISHING MAXHEAP
			//if associate index is same as tail
			if (associate_index==maxTail){
				maxHeap[maxTail--]= null; //simply remove tail, no need to re-establish
			}
			//if associate index != tail
			else{maxHeap[associate_index] = maxHeap[maxTail]; //swap tail with associate position
			maxHeap[maxTail--] = null; //clear tail
			maxHeap[associate_index].setIndex(associate_index); //set new index for swapped element
			downHeapMax(0);} //re-establish maxheap

			//SETTING BUFFER
			buffer = ret_ref; //set buffer as associate
			buffer.setIndex(-1); //clear index by making it -1

			//NOW RE-ESTABLISHING MINHEAP
			//If only 1 element in minHeap
			if( minTail == 0 ) {
				minTail       = -1; //make it empty
				minHeap [ 0 ] = null; //clear element
				return ret; //return maximum value
			}

			//If more than 1 element
			//REMOVE ROOT OF MINHEAP
			minHeap[ 0 ]     = minHeap [ minTail]; //swap tail with root
			minHeap [ minTail-- ] = null; //clear tail
			minHeap[0].setIndex(0); //reset index to root
			downHeapMin( 0 ); //re-establish minHeap
			return ret; //return min key that was removed
		}

		//If buffer is NOT EMPTY, needs comparison with ROOT

		//TEST FIRST TO REMOVE BUFFER IF ITS SMALLEST VALUE
		if ((Integer)ret.getKey()>(Integer)buffer.getKey()){ //Compares min root to buffer
			Entry <K,V> temp = buffer; //If buffer smaller
			buffer = null; //Clears buffer
			return temp; //Returns buffer
		}

		//OTHERWISE, REMOVE ROOT OF MINHEAP
		minHeap[ 0 ]     = minHeap [ minTail]; //swap tail with root
		minHeap [ minTail-- ] = null; //clear tail
		minHeap[0].setIndex(0); //reset index to root
		downHeapMin( 0 ); //re-establish minHeap

		//RE-PROCESSING
		//Set association between buffer and associated element
		buffer.setAssociate(ret_ref);
		ret_ref.setAssociate(buffer);

		//FIRST REMOVE ASSOCIATE FROM MAXHEAP
		int index_new = ret_ref.getIndex(); //get index of removed reference
		//if index_new == tail
		if (associate_index==maxTail){
			maxHeap[maxTail--]= null; //simply remove tail, no need to re-establish
		}
		//if index_new != tail
		else{maxHeap[index_new] = maxHeap[maxTail]; //swap ref with tail
			maxHeap[maxTail--] = null; //clear tail
			maxHeap[index_new].setIndex(index_new); //set new index
			downHeapMax(0);} //re-establish maxheap

		//If buffer is larger than associate
		if ((Integer) buffer.getKey()>(Integer) ret_ref.getKey()){
			//Add associate to minHeap
			minHeap[++minTail] = ret_ref;
			ret_ref.setIndex(minTail);
			upHeapMin(minTail);

			//Add buffer to maxHeap
			Entry<K,V> temp = buffer;
			buffer = null; //clear buffer
			maxHeap[++maxTail] = temp;
			temp.setIndex(maxTail);
			upHeapMax(maxTail); //re-establish maxHeap
			return ret; //return min key removed
		}
		//If buffer is smaller than associate
		//Add associate to maxHeap
		maxHeap[++maxTail] = ret_ref;
		ret_ref.setIndex(maxTail);
		upHeapMax(maxTail);

		//Add buffer to minHeap
		Entry<K,V> temp = buffer;
		buffer = null; //clear buffer
		minHeap[++minTail] = temp;
		temp.setIndex(minTail);
		upHeapMin(minTail);
		return ret; //return min key removed
	} /* removeMin */

	//Below is a added method for assignment 2 that was not previously implemented
	/**
	 * Removes and returns an entry with maximum key.
	 * O(log(n))
	 * @return the removed entry (or null if empty)
	 */
	public Entry <K, V> removeMax() {
		if( isEmpty() ) //If heaps are empty, return null
			return null;

		Entry ret = maxHeap[ 0 ]; //get max value in maxHeap
		Entry ret_ref = ret.getAssociate(); //get associate value of max from minHeap
		int associate_index = ret_ref.getIndex(); //index of associate

		//If buffer is EMPTY
		if (buffer == null){
			if (minTail==associate_index){ //if associate index is the tail
				minHeap[minTail--]=null; //clear tail
			}
			//if associate index != tail
			else{ minHeap[associate_index] = minHeap[minTail]; //swap tail with reference position
			minHeap[minTail--] = null; //clear tail
			minHeap[associate_index].setIndex(associate_index); //set new index
			downHeapMin(0);} //re-establish minheap

			buffer = ret_ref; //set buffer as reference
			buffer.setIndex(-1); //buffer index -1 to indicate not in heaps

			if( maxTail == 0 ) { //If only one element in maxHeap
				maxTail       = -1; //Now empty heap
				maxHeap [ 0 ] = null; //clear root
				return ret; //return maximum value
			}
			//If more than 1 elements
			//REMOVE ROOT OF MAXHEAP
			maxHeap[ 0 ]     = maxHeap [ maxTail]; //swap tail element with root element
			maxHeap [ maxTail-- ] = null; //clear tail element
			maxHeap[0].setIndex(0); //set index
			downHeapMax ( 0 ); //re-establish maxHeap
			return ret; //return max key removed
		}

		//If buffer is NOT EMPTY
		if ((Integer)ret.getKey()<(Integer)buffer.getKey()){ //Compares max root to buffer
			Entry temp = buffer; //If buffer larger
			buffer = null; //Removes buffer and returns it
			return temp;
		}

		//OTHERWISE, REMOVE ROOT OF MAXHEAP
		maxHeap[ 0 ]     = maxHeap [ maxTail]; //swap tail with root
		maxHeap [ maxTail-- ] = null; //clear tail
		maxHeap[0].setIndex(0); //reset index to root
		downHeapMax( 0 );

		//Set association between buffer and element to be reprocessed
		buffer.setAssociate(ret_ref);
		ret_ref.setAssociate(buffer);

		//Remove reference/associate from minHeap
		int index_new = ret_ref.getIndex(); //get index of removed reference

		if (minTail==index_new){ //if associate index is the tail
			minHeap[minTail--]=null; //clear tail
		}
		//if associate index != tail
		else{ minHeap[index_new] = minHeap[minTail]; //swap ref with tail
			minHeap[minTail--] = null; //clear tail
			minHeap[index_new].setIndex(index_new); //set new index
			downHeapMin(0);} //re-establish minheap

		if ((Integer) buffer.getKey()>(Integer) ret_ref.getKey()){ //if reference is smaller than buffer
			minHeap[++minTail] = ret_ref; //add reference to minHeap
			ret_ref.setIndex(minTail); //set index
			upHeapMin(minTail); //re-establish minHeap

			Entry temp = buffer; //add buffer to maxHeap
			buffer = null; //clear buffer
			maxHeap[++maxTail] = temp;
			temp.setIndex(maxTail); //set index
			upHeapMax(maxTail); //re-establish maxHeap
			return ret; //return max key removed
		}
		//if reference larger than buffer
		maxHeap[++maxTail] = ret_ref; //add reference to maxHeap
		ret_ref.setIndex(maxTail); //set index
		upHeapMax(maxTail); //re-establish maxHeap

		Entry temp = buffer; //add buffer to minHeap
		buffer = null; //clear buffer
		minHeap[++minTail] = temp;
		temp.setIndex(minTail); //set index
		upHeapMin(minTail); //re-establish minHeap
		return ret; //return max key removed
	} /* removeMax */


	/****************************************************
	*
	*           Methods for Heap Operations
	*
	****************************************************/

	/**
	 * Algorithm to place element after insertion at the tail used in maxHeap specifically.
	 * O(log(n))
	 */
	private void upHeapMax( int location ) {
		if( location == 0 ) return;

		int    parent = parent ( location );

		if( maxHeap [ parent ].key.compareTo ( maxHeap [ location ].key ) < 0 ) {
			swapMax ( location, parent );
			upHeapMax ( parent );
		}
	} /* upHeapMax */

	/**
	 * Algorithm to place element after insertion at the tail used in minHeap specifically.
	 * O(log(n))
	 */
	private void upHeapMin( int location ) {
		if( location == 0 ) return;

		int    parent = parent ( location );

		if( minHeap [ parent ].key.compareTo ( minHeap [ location ].key ) > 0 ) {
			swapMin ( location, parent );
			upHeapMin ( parent );
		}
	} /* upHeapMin */


	/**
	 * Algorithm to place element after removal of root and tail element placed at root used in minHeap specifically
	 * O(log(n))
	 */
	private void downHeapMin( int location ) {
		int    left  = (location * 2) + 1;
		int    right = (location * 2) + 2;

		//Both children null or out of bound
		if( left > minTail ) return;

		//left in right out;
		if( left == minTail ) {
			if( minHeap [ location ].key.compareTo ( minHeap [ left ].key ) > 0 )
				swapMin ( location, left );
			return;
		}

		int    toSwap = (minHeap [ left ].key.compareTo ( minHeap [ right ].key ) < 0) ?
		                left : right;

		if( minHeap [ location ].key.compareTo ( minHeap [ toSwap ].key ) > 0 ) {
			swapMin ( location, toSwap );
			downHeapMin ( toSwap );
		}
	} /* downHeapMin */

	/**
	 * Algorithm to place element after removal of root and tail element placed at root used in maxHeap specifically
	 * O(log(n))
	 */
	private void downHeapMax( int location ) {
		int    left  = (location * 2) + 1;
		int    right = (location * 2) + 2;

		//Both children null or out of bound
		if( left > minTail ) return;

		//left in right out;
		if( left == minTail ) {
			if( maxHeap [ location ].key.compareTo ( maxHeap [ left ].key ) < 0 )
				swapMax ( location, left );
			return;
		}

		int    toSwap = (maxHeap [ left ].key.compareTo ( maxHeap [ right ].key ) > 0) ?
				left : right;

		if( maxHeap [ location ].key.compareTo ( maxHeap [ toSwap ].key ) < 0 ) {
			swapMax ( location, toSwap );
			downHeapMax ( toSwap );
		}
	} /* downHeapMax */


	/**
	 * Find parent of a given location,
	 * Parent of the root is the root
	 * O(1)
	 */
	private int parent( int location ) {
		return (location - 1) / 2;
	} /* parent */


	/**
	 * Inplace swap of 2 elements, assumes locations are in minHeap array
	 * O(1)
	 */
	private void swapMin( int location1, int location2 ) {
		Entry <K, V>    temp = minHeap [ location1 ];
		minHeap [ location1 ] = minHeap [ location2 ];
		minHeap [ location2 ] = temp;
		
		minHeap[location1].index= location1;
		minHeap[location2].index= location2;
	} /* swapMin */

	/**
	 * Inplace swap of 2 elements, assumes locations are in maxHeap array
	 * O(1)
	 */
	private void swapMax( int location1, int location2 ) {
		Entry <K, V>    temp = maxHeap [ location1 ];
		maxHeap [ location1 ] = maxHeap [ location2 ];
		maxHeap [ location2 ] = temp;

		maxHeap[location1].index= location1;
		maxHeap[location2].index= location2;
	} /* swapMax */

	/**
	 * Prints information on DEPQ size, minHeap/maxHeap size along with all the elements in the heaps
	 */
    public void print() {
    	System.out.println("DEPQ total size: "+this.size()); //print total size of DEPQ
		System.out.println("-------------------------------------------------------------");
		System.out.println("minHeap size: "+(minTail+1)); //print minheap size
		for (int i=0;i< (minTail+1);i++) //print values from minHeap
		  System.out.println ( "(" + minHeap[i].key.toString() + "," + minHeap[i].value.toString() + ":" + minHeap[i].index + "), " );
		System.out.println("-------------------------------------------------------------");
		if (buffer!=null){ //print buffer
			System.out.println("buffer: ("+ buffer.key.toString()+","+buffer.value.toString()+","+buffer.index+")");}
		else{System.out.println("buffer = null");}
		System.out.println("-------------------------------------------------------------");
		System.out.println("maxHeap size: "+(maxTail+1)); //print maxheap size
		for (int i=0;i< (maxTail+1);i++) //print values from maxHeap
			System.out.println ( "(" + maxHeap[i].key.toString() + "," + maxHeap[i].value.toString() + ":" + maxHeap[i].index + "), " );
		System.out.println("=============================================================");
	}
}



