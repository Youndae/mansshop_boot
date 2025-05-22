//페이지네이션 및 검색 관련 Module
import { buildQueryString } from './queryStringUtils';
import { PAGINATION_TYPE } from '../constants/paginationTypes';

// 페이지네이션에서 사용될 pagingObject 객체 생성
const createPagingObject = (page, elementSize, totalPages) => {
    let endPage = Number(Math.ceil(page / elementSize) * elementSize);
    const startPage = endPage - (elementSize - 1);

    if(totalPages < endPage)
        endPage = totalPages;

    return {
        startPage,
        endPage,
        prev: startPage > 1,
        next: endPage < totalPages,
        activeNo: page
    };
}


// 페이지네이션의 ElementSize가 10인 기본 설정
export const mainProductPagingObject = (page = 1, totalPages) =>
    createPagingObject(page, 10, totalPages);
// 페이지네이션의 ElementSize가 5인 경우
export const productDetailPagingObject = (page = 1, totalPages) =>
    createPagingObject(page, 5, totalPages);

// 각 페이지네이션 버튼 클릭 시 페이지 번호 반환
const getPrevNumber = (pagingData) => pagingData.startPage - 1;
const getNextNumber = (pagingData) => pagingData.endPage + 1;

// 페이지네이션 버튼 클릭 이벤트 객체 생성
const buildQueryForm = ({ type, page, keyword, searchType, term }) => {
	return {
		...(type !== undefined && { type }),
		...(page !== undefined && { page }),
		...(keyword !== undefined && { keyword }),
		...(searchType !== undefined && { searchType }),
		...(term !== undefined && { term })
	};
};

// 검색 타입만 존재하고 keyword가 없는 경우 undefined 반환
// 검색 타입 상태값이 기본값으로 동작하기 때문에 불필요한 SearchType 값이 전달되는 것을 방지
export const validateSearchType = ({keyword, searchType}) => keyword ? searchType : undefined;

export const handlePageChange = ({typeOrNumber, pagingData, navigate, listType, keyword, searchType, term}) => {
	const targetPage = getClickPageNumber(typeOrNumber, pagingData);
	const searchTypeValue = validateSearchType({keyword, searchType});
	const queryObject = buildQueryForm({
		type: listType,
		page: targetPage,
		keyword,
		searchType: searchTypeValue,
		term
	});
	const queryString = buildQueryString(queryObject);
	navigate(`${queryString}`);
}

export const getClickPageNumber = (typeOrNumber, pagingData) => {
	let targetPage;

	if(typeOrNumber === PAGINATION_TYPE.PREV)
		targetPage = getPrevNumber(pagingData);
	else if(typeOrNumber === PAGINATION_TYPE.NEXT)
		targetPage = getNextNumber(pagingData);
	else if(typeof typeOrNumber === 'number')
		targetPage = typeOrNumber;
	else
		return;

	return targetPage;
}