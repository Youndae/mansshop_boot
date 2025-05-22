import { useState } from 'react';
import { PATTERNS } from '../constants/patterns';

export const CheckResult = {
	short: 'short',
	invalid: 'invalid',
	valid: 'valid',
}

export default function usePasswordValidator() {
	const [pwCheck, setPwCheck] = useState(null);
	const [verifyPw, setVerifyPw] = useState(null);
	const [pwCheckInfo, setPwCheckInfo] = useState(false);

	const validatePassword = (value) => {
		if (value.length < 8) {
			setPwCheck(CheckResult.short);
			return false;
		} else if (!PATTERNS.PASSWORD.test(value)) {
			setPwCheck(CheckResult.invalid);
			return false;
		} else {
			setPwCheck(CheckResult.valid);
			return true;
		}
	};

	const validatePasswordCheck = (password, checkPassword) => {
		if(password === checkPassword)
			setVerifyPw(CheckResult.valid);
		else
			setVerifyPw(CheckResult.invalid);
	}

	const validateAndSyncPassword = (name, value, userData) => {
		if (name === 'userPw') {
			if (validatePassword(value)) 
				setPwCheckInfo(true);
			else
				setPwCheckInfo(false);
			
			validatePasswordCheck(value, userData.checkPassword);
		} else if (name === 'checkPassword') 
			validatePasswordCheck(userData.userPw, value);
	};

	return {
		pwCheck,
		verifyPw,
		pwCheckInfo,
		validatePassword,
		validatePasswordCheck,
		validateAndSyncPassword,
	}
}