
export const mainProductPagingObject = (pageNum, totalPages) => {
    return createPagingObject(pageNum, 12, totalPages);
}

export const productDetailPagingObject = (pageNum, totalPages) => {
    return createPagingObject(pageNum, 10, totalPages);
}








const createPagingObject = (pageNum, elementSize,totalPages) => {
    let endPage = Number(Math.ceil(pageNum / elementSize) * elementSize);
    const startPage = endPage - 9;
    if(totalPages < endPage)
        endPage = totalPages;

    const prev = startPage > 1;
    const next = endPage < totalPages;

    return {
        startPage: startPage,
        endPage: endPage,
        prev: prev,
        next: next,
    }
}

export function handlePageNumBtn (e, navigate, keyword) {
    const clickNo = e.target.textContent;

    paginationNavigate(clickNo, keyword, navigate);
}

export function handlePrevBtn (startPage, navigate, keyword) {
    const prevNumber = startPage - 1;

    paginationNavigate(prevNumber, keyword, navigate);
}

export function handleNextBtn (endPage, navigate, keyword) {
    const nextNumber = endPage + 1;

    paginationNavigate(nextNumber, keyword, navigate);
}

const paginationNavigate = (clickNo, navigate) => {
    navigate(`?page=${clickNo}`);
}