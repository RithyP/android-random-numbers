package com.rithyphavan.randomnumber

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<BINDING : ViewBinding, T>(
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> BINDING, mData: List<T> = listOf()
) : RecyclerView.Adapter<BaseAdapter.VH<BINDING, T>>(), Filterable {

    protected var mData = mutableListOf<T>()

    protected var selectedItem: T? = null
    protected var previousSelectedNumber: T? = null


    private var totalItems: Int = 0
    private var lastOrFirstVisibleItem = 0
    var isLoading = false
    var isLoadAllItemDone = false

    /**
     * Load more
     */
    var onLoadMoreItems: (() -> (Unit))? = null
    var onScrolled: ((recyclerView: RecyclerView, dx: Int, dy: Int) -> (Unit))? = null

    var onFilterDone: (() -> (Unit))? = null

    init {
        this.mData.addAll(mData)
    }

    override fun getItemCount() = mData.size

    /**
     * Override should load
     */
    protected open fun shouldLoadMore(): Boolean = false

    protected open fun visibleThreshold(): Int = 5

    protected open fun isLoadMoreAtBottom() = true

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                onScrolled?.invoke(recyclerView, dx, dy)
                if (shouldLoadMore()) {
                    if ((recyclerView.layoutManager as LinearLayoutManager).orientation == LinearLayoutManager.VERTICAL) {
                        if (isLoadMoreAtBottom()) {
                            //Load more at top
                            if (dy > 0) {
                                lastOrFirstVisibleItem =
                                    (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                                totalItems = recyclerView.layoutManager?.itemCount ?: 0
                                if (totalItems <= visibleThreshold() + lastOrFirstVisibleItem && !isLoading && !isLoadAllItemDone) {
                                    //More
                                    isLoading = true
                                    onLoadMoreItems?.invoke()
                                }
                            }
                        } else {
                            //Load more at bottom
                            if (dy < 0) {
                                lastOrFirstVisibleItem =
                                    (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                                if (lastOrFirstVisibleItem <= visibleThreshold()) {
                                    //More
                                    isLoading = true
                                    onLoadMoreItems?.invoke()
                                }
                            }

                        }
                    } else {
                        //Scroll Horizontal
                        if (dx > 0) {
                            //Scroll right
                            lastOrFirstVisibleItem =
                                (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                            totalItems = recyclerView.layoutManager?.itemCount ?: 0
                            if (totalItems <= visibleThreshold() + lastOrFirstVisibleItem && !isLoading && !isLoadAllItemDone) {
                                //More
                                isLoading = true
                                onLoadMoreItems?.invoke()
                            }
                        } else {
                            //Scroll left
                            lastOrFirstVisibleItem =
                                (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                            if (lastOrFirstVisibleItem <= visibleThreshold()) {
                                //More
                                isLoading = true
                                onLoadMoreItems?.invoke()
                            }
                        }

                    }
                }

            }
        })

    }

    fun resetLoadingState() {
//        isLoading = false
        isLoadAllItemDone = false
    }

    fun addItemAtFirst(data: T) {
        mData.add(0, data)
        notifyItemChanged(mData.size)
    }

    fun addItem(data: T) {
        mData.add(data)
        notifyItemChanged(mData.size)
    }

    fun addItems(data: List<T>) {
        val oldDataSize = mData.size
        mData.addAll(data)
        notifyItemRangeInserted(oldDataSize, data.size + mData.size)
    }

    fun removeItems(start: Int, end: Int) {
        mData.removeAll(mData.subList(start, end).toSet())
        notifyItemRangeRemoved(start, end)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(mData: List<T>) {
        this.mData.clear()
        this.mData.addAll(mData)
        notifyDataSetChanged()
    }

    /**
     * All all data from other list and notify adapter
     * @param newData other list
     * @param isClear is true clear old list
     */
    fun addAllAndNotify(newData: List<T>, isClear: Boolean = true) {
        mData.apply {
            if (isClear) {
                this.clear()
            }
            this.addAll(newData)
        }
        notifyDataSetChanged()
    }

    fun setNewSelectedItemAndUnselectCurrent(item: T?) {
        this.previousSelectedNumber = selectedItem
        this.selectedItem = item
        notifyClearOldItemAndSelectedNew(previousSelectedNumber, selectedItem)
    }

    fun clearSelectedItemValue(animate: Boolean = true) {
        if (animate) {
            notifyItemChanged(mData.indexOf(selectedItem))
        } else {
            notifyDataSetChanged()
        }
        selectedItem = null
    }

    fun notifyClearOldItemAndSelectedNew(oldItem: T?, newItem: T?) {
        notifyItemChanged(mData.indexOf(oldItem))
        notifyItemChanged(mData.indexOf(newItem))
    }

    fun getItems(): List<T> {
        return mData
    }

    open fun getItem(position: Int): T {
        return mData[position]
    }

    fun getSelectItem() = selectedItem

    override fun onBindViewHolder(holder: VH<BINDING, T>, position: Int) {
        val item = mData[position]
        holder.itemView.tag = item
        onBind(holder, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH<BINDING, T> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = inflate.invoke(inflater, parent, false)
        return VH(binding)
    }

    /**
     * Override filter to filter data when search
     */
    override fun getFilter(): Filter? {
        return null
    }

    abstract fun onBind(holder: VH<BINDING, T>, item: T)

    open class VH<BINDING : ViewBinding, T>(var binding: BINDING) :
        RecyclerView.ViewHolder(binding.root)


}