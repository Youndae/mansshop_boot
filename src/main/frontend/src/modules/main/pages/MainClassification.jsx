import React, { useEffect, useState } from "react";
import { useParams, useSearchParams, useNavigate } from "react-router-dom";

import { mainProductPagingObject } from "../../../common/utils/paginationUtils";
import { handlePageChange } from "../../../common/utils/paginationUtils";
import { getClassificationList } from "../services/mainService";

import MainContent from "../components/MainContent";
import Pagination from "../../../common/components/Pagination";

/*
    분류별 상품 목록 페이지
    선택한 상품 분류에 따라 해당하는 상품 목록 출력
 */
function MainClassification() {
	const { classification } = useParams();
	const [params] = useSearchParams();
	const { page } = Object.fromEntries(params);

	const [data, setData] = useState([]);
	const [pagingData, setPagingData] = useState({
		startPage: 0,
		endPage: 0,
		prev: false,
		next: false,
		activeNo: page,
	});

	const navigate = useNavigate();

	useEffect(() => {
		window.scrollTo(0, 0);

		const classificationList = async () => {
			try {
				const res = await getClassificationList(classification, page);
				setData(res.data.content);
				const pagingData = mainProductPagingObject(page, res.data.totalPages);
				setPagingData({
					startPage: pagingData.startPage,
					endPage: pagingData.endPage,
					prev: pagingData.prev,
					next: pagingData.next,
					activeNo: pagingData.activeNo,
				});
			} catch (err) {
				console.log(err);
			}
		}

		classificationList();
	}, [page, classification]);
	
	const handlePageBtn = (type) => {
		handlePageChange({
			typeOrNumber: type,
			pagingData,
			navigate,
		});
	}

	return (
        <>
            <MainContent
                data={data}
                classification={classification}
            />
			<Pagination
				pagingData={pagingData}
				className={null}
				handlePageBtn={handlePageBtn}
			/>
        </>
    )
}

export default MainClassification;
	