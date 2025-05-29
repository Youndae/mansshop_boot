import React, {useEffect, useState} from 'react';

import { getFailedOrderDataList, postRetryOrderData } from "../../services/adminDataService";
import {RESPONSE_MESSAGE} from "../../../../common/constants/responseMessageType";
import { numberComma } from "../../../../common/utils/formatNumberComma";

import AdminSideNav from "../../components/AdminSideNav";
import DefaultButton from "../../../../common/components/DefaultButton";

function AdminFailedOrderList() {
    const [dataCount, setDataCount] = useState(0);

    useEffect(() => {
        const getFailedOrderData = async () => {
            try {
                const res = await getFailedOrderDataList();
                console.log('failed count res : ', res);
                setDataCount(res.data.id);
            }catch (err) {
                console.log(err);
            }
        }

        getFailedOrderData();
    }, []);

    //재처리 버튼 이벤트
    const handleOnClick = async () => {
        try {
            const res = await postRetryOrderData();

            if(res.data.message === RESPONSE_MESSAGE.OK)
                alert('모든 메시지가 재처리되었습니다.');
        }catch (err) {
            if(err.response.status === 441) {
                alert('메시지 재처리가 실패했습니다.')
            }
        }
    }

    return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'data'}
            />
        {/*
        리스트 폼
        실패 데이터 개수 출력하고 재처리 버튼 추가.
          */}
            <div className="admin-content">
                <div className="admin-content-header admin-product-header">
                    <h1>처리 실패 주문</h1>
                    <DefaultButton
                        btnText={'재시도'}
                        onClick={handleOnClick}
                    />
                </div>
                <div className="admin-content-content">
                    <h3>실패 데이터 수 : {numberComma(dataCount)}</h3>
                </div>
            </div>
        </div>
    )
}

export default AdminFailedOrderList;