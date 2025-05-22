import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';

import { getDiscountProductList } from '../../services/adminProductService';
import { buildQueryString } from '../../../../common/utils/queryStringUtils';
import { mainProductPagingObject } from '../../../../common/utils/paginationUtils';
import { handlePageChange } from '../../../../common/utils/paginationUtils';
import { numberComma } from '../../../../common/utils/formatNumberComma';

import AdminSideNav from '../../components/AdminSideNav';
import DefaultButton from '../../../../common/components/DefaultButton';
import Pagination from '../../../../common/components/Pagination';

/*
	상품 할인 설정.

	분류, 상품명, 가격, 할인율을 테이블 구조로 출력하고
	상단에는 할인 상품 설정 버튼을 생성한다.

	할인을 하지 않고 있는 상품은 테이블에 출력하지 않는다.
*/
function AdminDiscountProductList() {
	const [params] = useSearchParams();
	const { page, keyword } = Object.fromEntries(params);

	const [data, setData] = useState([]);
    const [pagingData, setPagingData] = useState({
        startPage: 0,
        endPage: 0,
        prev: false,
        next: false,
        activeNo: page,
    });
    const [keywordInput, setKeywordInput] = useState('');

    const navigate = useNavigate();

	useEffect(() => {
		const getList = async() => {
			try {
				const res = await getDiscountProductList(page, keyword);

				setData(res.data.content);
                const pagingObject = mainProductPagingObject(page, res.data.totalPages);

                setPagingData({
                    startPage: pagingObject.startPage,
                    endPage: pagingObject.endPage,
                    prev: pagingObject.prev,
                    next: pagingObject.next,
                    activeNo: pagingObject.activeNo,
                });
			} catch(err) {
				console.log(err);
			}
		}

		getList();
	}, [page, keyword]);
	
	//페이지네이션 버튼 이벤트
	const handlePageBtn = (type) => {
		handlePageChange({
			typeOrNumber: type,
			pagingData,
			navigate,
			keyword,	
		})
	}

	//검색어 input 입력 이벤트
    const handleKeywordOnChange = (e) => {
        setKeywordInput(e.target.value);
    }

	// 검색 버튼 이벤트
	const handleSearchOnClick = async () => {
		const queryString = buildQueryString({
			keyword: keywordInput,
		});
		navigate(`${queryString}`);
	}

	// 할인 상품 설정 버튼 이벤트
	const handleDiscountProductOnClick = () => {
		navigate('/admin/product/discount/setting');
	}

	return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'product'}
            />
            <div className="admin-content">
                <div className="admin-content-header">
                    <h1>할인 상품 목록</h1>
                    <Link to={'/admin/product/discount/setting'}>
                        <DefaultButton
                            btnText={'할인 추가'}
                            className={'discount-btn'}
                            onClick={handleDiscountProductOnClick}
                        />
                    </Link>
                </div>
                <div className="admin-content-content">
                    <table className="admin-content-table product-stock-table">
                        <thead>
                            <tr>
                                <th>분류</th>
                                <th>상품명</th>
                                <th>가격</th>
                                <th>할인율</th>
                                <th>판매가</th>
                            </tr>
                        </thead>
                        <tbody>
                            {data.map((data, index) => {
                                return (
                                    <tr key={index}>
                                        <td>{data.classification}</td>
                                        <td>
                                            <Link to={`/admin/product/${data.productId}`}>
                                                {data.productName}
                                            </Link>
                                        </td>
                                        <td>{numberComma(data.price)}</td>
                                        <td>{data.discount} %</td>
                                        <td>{numberComma(data.totalPrice)}</td>
                                    </tr>
                                )
                            })}
                        </tbody>
                    </table>
                </div>
                <div className="admin-search">
                    <input type={'text'} onChange={handleKeywordOnChange} value={keywordInput || ''}/>
                    <img alt={''} src={"https://as1.ftcdn.net/v2/jpg/03/25/73/68/1000_F_325736897_lyouuiCkWI59SZAPGPLZ5OWQjw2Gw4qY.jpg"} onClick={handleSearchOnClick}/>
                    <Pagination
                        pagingData={pagingData}
                        handlePageBtn={handlePageBtn}
                        className={'like-paging'}
                    />
                </div>
            </div>
        </div>
    )
}

export default AdminDiscountProductList;