/**
 * A page-based ArrayList
 * * Pages are NOT index-based (they start at 1, not 0)
 * * A page sublist is created when you call a method to get one; it is not created and then updated accordingly.
 * @param T Type of the ArrayList
 *
 * Copied from Hysentials from Skyblock Sandbox Recode
 */
class PaginationList<T> : ArrayList<T> {
    var elementsPerPage: Int

    constructor(collection: Collection<T>, elementsPerPage: Int) : super(collection) {
        this.elementsPerPage = elementsPerPage
    }

    /**
     * Defines a new Pagination List.
     * @param elementsPerPage How many elements will be included on a page of the list.
     */
    constructor(elementsPerPage: Int) : super() {
        this.elementsPerPage = elementsPerPage
    }

    /**
     * Defines a new Pagination List.
     * @param elementsPerPage How many elements will be included on a page of the list.
     * @param elements Elements to add to the list immediately.
     */
    constructor(elementsPerPage: Int, vararg elements: T) : super(elements.asList()) {
        this.elementsPerPage = elementsPerPage
    }

    /**
     * @return The number of pages this list holds.
     */
    fun getPageCount(): Int {
        return kotlin.math.ceil(size.toDouble() / elementsPerPage.toDouble()).toInt()
    }

    /**
     * Get a page from the list.
     * @param page Page you want to access.
     * @return A sublist of only the elements from that page.
     */
    fun getPage(page: Int): List<T>? {
        if (page < 1 || page > getPageCount()) return null
        val startIndex = (page - 1) * elementsPerPage
        val endIndex = kotlin.math.min(startIndex + elementsPerPage, this.size)
        return subList(startIndex, endIndex)
    }

    /**
     * @return A 2D List with every page.
     */
    fun getPages(): List<List<T>> {
        val pages = mutableListOf<List<T>>()
        for (i in 1..getPageCount()) {
            getPage(i)?.let { pages.add(it) }
        }
        return pages
    }

    fun addAll(elements: Array<T>) {
        this.addAll(elements)
    }

    override fun toString(): String {
        val res = StringBuilder()
        for (i in 1..getPageCount()) {
            res.append("Page ").append(i).append(": ").append("\n")
            getPage(i)?.forEach { element ->
                res.append(" - ").append(element).append("\n")
            }
        }
        return res.toString()
    }
}