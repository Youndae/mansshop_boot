import React, { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';

import { mainProductPagingObject } from '../../../common/utils/paginationUtils';
import { handlePageChange } from '../../../common/utils/paginationUtils';
import { getSearchProductList } from '../services/mainService';

import Pagination from '../../../common/components/Pagination';
import MainContent from '../components/MainContent';

//상품 검색 컴포넌트
function SearchProduct() {
	const [params] = useSearchParams();
	const { page, keyword } = Object.fromEntries(params);

	const [data, setData] = useState([]);
	const [pagingData, setPagingData] = useState({
		startPage: 0,
		endPage: 0,
		prev: false,
		next: false,
		activeNo: page,
	})

	const navigate = useNavigate();
	
	useEffect(() => {
		window.scrollTo(0, 0);
		const getSearchProduct = async() => {
			try{
				const res = await getSearchProductList(page, keyword);
				setData(res.data.content);
				const pagingData = mainProductPagingObject(page, res.data.totalPages);
				setPagingData({
					startPage: pagingData.startPage,
					endPage: pagingData.endPage,
					prev: pagingData.prev,
					next: pagingData.next,
					activeNo: pagingData.activeNo,
				});
			}catch(err) {
				console.log(err);
			}
		}
		getSearchProduct();
	}, [page, keyword]);
	
	//페이지네이션 버튼 이벤트
	const handlePageBtn = (type) => {
		handlePageChange({
			typeOrNumber: type,
			pagingData,
			navigate,
			keyword
		})
	}

	return (
		<>
            <MainContent
                data={data}
                classification={''}
            />
            <Pagination
                pagingData={pagingData}
                handlePageBtn={handlePageBtn}
            />
        </>
	)
}

export default SearchProduct;