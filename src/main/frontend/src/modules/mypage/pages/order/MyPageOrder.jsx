import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';

import { getOrderList } from '../../services/mypageOrderService';
import { mainProductPagingObject } from '../../../../common/utils/paginationUtils';
import { handlePageChange } from '../../../../common/utils/paginationUtils';

import MyPageSideNav from '../../components/MyPageSideNav';
import OrderListForm from '../../../../common/components/OrderListForm';

import '../../../../styles/mypage.css';

/*
    주문 목록 조회
    select box를 통한 기간별 조회 가능.
    3, 6, 12 개월, 전체 조회 가능.
*/
function MyPageOrder() {
	const [ params ] = useSearchParams();
	const { page, term = 3 } = Object.fromEntries(params);

	const [orderData, setOrderData] = useState([]);
    const [pagingData, setPagingData] = useState({
        startPage: 0,
        endPage: 0,
        prev: false,
        next: false,
        activeNo: page,
    });

    const userType = 'user';

    const navigate = useNavigate();

	useEffect(() => {
		const getOrderData = async(term) => {
			try {
				const res = await getOrderList(term, page);

				const pagingObject = mainProductPagingObject(page, res.data.totalPages);

                setPagingData({
                    startPage: pagingObject.startPage,
                    endPage: pagingObject.endPage,
                    prev: pagingObject.prev,
                    next: pagingObject.next,
                    activeNo: pagingObject.activeNo,
                });

                setOrderData(res.data.content);
			} catch (error) {
				console.log(error);
			}
		}

		getOrderData(term);
	}, [term, page]);

	//페이지네이션 버튼 이벤트
	const handlePageBtn = (type) => {
		handlePageChange({
			typeOrNumber: type,
			pagingData,
			navigate,
			term,
		});
	}

	//기간 select box 이벤트
	const handleSelectOnChange = (e) => {
		const selectTerm = e.target.value;

		navigate(`?term=${selectTerm}`);
	}

	return (
        <div className="mypage">
            <MyPageSideNav
                qnaStat={false}
            />
            <OrderListForm
                className={'mypage-content'}
                orderData={orderData}
                pagingData={pagingData}
                term={term}
                userType={userType}
                handleSelectOnChange={handleSelectOnChange}
                handlePageBtn={handlePageBtn}
            />
        </div>
    )
}

export default MyPageOrder;