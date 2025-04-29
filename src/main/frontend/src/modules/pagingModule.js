//페이지네이션 및 검색 관련 Module
//검색과 페이지네이션이 같이 처리되는 경우가 있기 때문에 해당 모듈에 같이 배치

//페이지네이션에 사용될 PagingObject 객체 생성. 기본 설정
export const mainProductPagingObject = (pageNum, totalPages) => {

    return createPagingObject(checkPageNum(pageNum), 10, totalPages);
}

//페이지네이션에 사용될 PagingObject 객체 생성. ElementSize가 5인 경우.
export const productDetailPagingObject = (pageNum, totalPages) => {
    return createPagingObject(checkPageNum(pageNum), 5, totalPages);
}

//페이지네이션에 필요한 prev, next 여부, 전체 페이지 등 연산 후 객체 반환
const createPagingObject = (pageNum, elementSize, totalPages) => {
    let endPage = Number(Math.ceil(pageNum / elementSize) * elementSize);
    const startPage = endPage - (elementSize - 1);
    if(totalPages < endPage)
        endPage = totalPages;

    const prev = startPage > 1;
    const next = endPage < totalPages;

    return {
        startPage: startPage,
        endPage: endPage,
        prev: prev,
        next: next,
        activeNo: pageNum
    }
}

//pageNum이 null로 들어오는 경우에 대응하기 위해 없는 경우 1로 반환
const checkPageNum = (pageNum) => {
    return pageNum === null ? 1 : pageNum
}

//사용자가 클릭한 페이지네이션 버튼 번호 반환
export const getClickNumber = (e) => {
    return e.target.textContent;
}

//사용자가 이전 버튼 클릭 시 처리될 끝 번호 반환
export const getPrevNumber = (pagingData) => {
    return pagingData.startPage - 1;
}

//사용자가 다음 버튼 클릭 시 처리될 다음 번호 반환
export const getNextNumber = (pagingData) => {
    return pagingData.endPage + 1;
}

//페이지네이션 버튼 클릭 이벤트 처리
export const pageSubmit = (page, navigate) => {
    navigate(`?page=${page}`);
}

//검색 이벤트 처리
export const searchSubmit = (keyword, navigate) => {
    navigate(`?keyword=${keyword}`);
}

//리스트 타입 ( all, new와 같이 목록이 분리되는 경우 ) 과 페이지네이션 처리 이벤트
export const typePageSubmit = (type, page, navigate) => {
    navigate(`?type=${type}&page=${page}`);
}

//검색 타입이 존재하는 검색 이벤트 처리
export const searchTypeSubmit = (type, keyword, navigate) => {
    navigate(`?type=${type}&keyword=${keyword}`);
}

//검색 타입이 없는 검색 후 페이지네이션 버튼 처리 이벤트
export const searchPageSubmit = (keyword, page, navigate) => {
    navigate(`?keyword=${keyword}&page=${page}`);
}

//검색 타입이 존재하는 검색 후 페이지네이션 버튼 처리 이벤트
export const searchTypePageSubmit = (type, keyword, page, navigate) => {
    navigate(`?type=${type}&keyword=${keyword}&page=${page}`);
}