
export const createPagingObject = (pageNum, totalPages) => {
    let endPage = Number(Math.ceil(pageNum / 10.0) * 10);
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