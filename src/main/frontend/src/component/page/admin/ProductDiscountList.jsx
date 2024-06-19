import React, {useEffect, useState} from 'react';
import {useDispatch, useSelector} from "react-redux";
import {useNavigate, useSearchParams} from "react-router-dom";
import {axiosInstance} from "../../../modules/customAxios";
import AdminSideNav from "../../ui/nav/AdminSideNav";


function ProductDiscountList() {
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
        getDiscountProduct();
    }, [page, keyword]);

    const getDiscountProduct = async () => {
        await axiosInstance.get(`admin/product/discount?keyword=${keyword}&page=${page}`)
            .then(res => {
                console.log('discount res : ', res);
            })
            .catch(err => {
                console.error('discount Error : ', err);
            })
    }

    return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'product'}
            />
            <div className="admin-content">
                <div className="admin-content-header">
                    <h1>할인 상품 목록</h1>
                </div>
            </div>
        </div>
    )
}

export default ProductDiscountList;