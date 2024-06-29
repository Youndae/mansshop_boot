import React, {useEffect, useState} from 'react';
import {useDispatch, useSelector} from "react-redux";
import {Link, useNavigate, useSearchParams} from "react-router-dom";
import {axiosInstance} from "../../../modules/customAxios";
import AdminSideNav from "../../ui/nav/AdminSideNav";
import {setMemberObject} from "../../../modules/loginModule";
import {
    getClickNumber,
    getNextNumber,
    getPrevNumber,
    pageSubmit,
    productDetailPagingObject, searchPageSubmit, searchSubmit
} from "../../../modules/pagingModule";
import Paging from "../../ui/Paging";
import {numberComma} from "../../../modules/numberCommaModule";
import DefaultBtn from "../../ui/DefaultBtn";


/*
        상품 할인 설정.

        분류, 상품명, 가격, 할인율을 테이블 구조로 출력하고
        상단에는 할인 상품 설정 버튼을 생성한다.

        할인을 하지 않고 있는 상품은 테이블에 출력하지 않는다.
 */
function AdminDiscount() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const [params] = useSearchParams();
    const page = params.get('page') == null ? 1 : params.get('page');
    const keyword = params.get('keyword');
    const [data, setData] = useState([]);
    const [pagingData, setPagingData] = useState({
        startPage: 0,
        endPage: 0,
        prev: false,
        next: false,
        activeNo: page,
    });
    const [keywordInput, setKeywordInput] = useState('');

    const dispatch = useDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        setKeywordInput(keyword);
        getDiscountProduct();
    }, [page, keyword]);

    const getDiscountProduct = async () => {
        await axiosInstance.get(`admin/product/discount?keyword=${keyword}&page=${page}`)
            .then(res => {
                setData(res.data.content);

                const pagingObject = productDetailPagingObject(page, res.data.totalPages);

                setPagingData({
                    startPage: pagingObject.startPage,
                    endPage: pagingObject.endPage,
                    prev: pagingObject.prev,
                    next: pagingObject.next,
                    activeNo: page,
                });

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);
            })
            .catch(err => {
                console.error('discount Error : ', err);
            })
    }

    const handlePageBtn = (e) => {
        handlePagingSubmit(getClickNumber(e));
    }

    const handlePagePrev = () => {
        handlePagingSubmit(getPrevNumber(pagingData));
    }

    const handlePageNext = () => {
        handlePagingSubmit(getNextNumber(pagingData));
    }

    const handlePagingSubmit = (pageNum) => {
        if(keyword == null)
            pageSubmit(pageNum, navigate);
        else
            searchPageSubmit(keyword, pageNum, navigate);
        /*if(keyword == null)
            navigate(`/admin/product/discount?page=${pageNum}`);
        else
            navigate(`/admin/product/discount?keyword=${keyword}&page=${pageNum}`);*/
    }

    const handleKeywordOnChange = (e) => {
        setKeywordInput(e.target.value);
    }

    const handleSearchOnClick = async () => {
        // navigate(`/admin/product/discount?keyword=${keywordInput}`);
        searchSubmit(keywordInput, navigate);
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
                        <DefaultBtn
                            btnText={'할인 추가'}
                            className={'discount-btn'}
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
                    <Paging
                        pagingData={pagingData}
                        onClickNumber={handlePageBtn}
                        onClickPrev={handlePagePrev}
                        onClickNext={handlePageNext}
                        className={'like-paging'}
                    />
                </div>
            </div>
        </div>
    )
}

export default AdminDiscount;