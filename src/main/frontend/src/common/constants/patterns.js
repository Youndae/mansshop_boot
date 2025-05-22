export const PATTERNS = {
	USERID: /^[A-Za-z0-9]{5,15}$/,
    PASSWORD: /^(?=.*[a-zA-Z])(?=.*[!@#$%^&*+=-])(?=.*[0-9]).{8,16}$/,
    EMAIL: /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i,
    PHONE: /^01(?:0|1|6|9)([0-9]{3,4})([0-9]{4})$/
}

export const REGEX = {
	PHONE: "(\\d{3})(\\d{3,4})(\\d{4})",
}