






import axios from 'axios'
import type {
  AxiosInstance,
  AxiosError,
  AxiosRequestConfig,
  AxiosResponse,
} from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import { ResultEnum } from '@/enums/httpEnums'
import { ResultData } from './type'
import { LOGIN_URL } from '@/config/config'
import { RESEETSTORE } from '../reset'
import router from '@/router'

const service: AxiosInstance = axios.create({
  // 判断环境设置不同的baseURL
  baseURL: '/mock',
  timeout: ResultEnum.TIMEOUT as number,
})

/**
 * @description: 请求拦截器
 * @returns {*}
 */
service.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    const token = userStore.token
    if (token) {
      config.headers.token = token
    }
    return config
  },
  (error: AxiosError) => {
    ElMessage.error(error.message)
    return Promise.reject(error)
  },
)
/**
 * @description: 响应拦截器
 * @returns {*}
 */
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const { data } = response
    // * 登陆失效（code == 203）
    if (data.code === ResultEnum.EXPIRE) {
      RESEETSTORE()
      ElMessage.error(data.message || ResultEnum.ERRMESSAGE)
      router.replace(LOGIN_URL)
      return Promise.reject(data)
    }

    if (data.code && data.code !== ResultEnum.SUCCESS) {
      ElMessage.error(data.message || ResultEnum.ERRMESSAGE)
      return Promise.reject(data)
    }
    return data
  },
  (error: AxiosError) => {
    // 处理 HTTP 网络错误
    let message = ''
    // HTTP 状态码
    const status = error.response?.status
    switch (status) {
      case 401:
        message = 'token 失效，请重新登录'
        break
      case 403:
        message = '拒绝访问'
        break
      case 404:
        message = '请求地址错误'
        break
      case 500:
        message = '服务器故障'
        break
      default:
        message = '网络连接故障'
    }

    ElMessage.error(message)
    return Promise.reject(error)
  },
)

/**
 * @description: 导出封装的请求方法
 * @returns {*}
 */
const http = {
  get<T>(
    url: string,
    params?: object,
    config?: AxiosRequestConfig,
  ): Promise<ResultData<T>> {
    return service.get(url, { params, ...config })
  },

  post<T>(
    url: string,
    data?: object,
    config?: AxiosRequestConfig,
  ): Promise<ResultData<T>> {
    return service.post(url, data, config)
  },

  put<T>(
    url: string,
    data?: object,
    config?: AxiosRequestConfig,
  ): Promise<ResultData<T>> {
    return service.put(url, data, config)
  },

  delete<T>(
    url: string,
    data?: object,
    config?: AxiosRequestConfig,
  ): Promise<ResultData<T>> {
    return service.delete(url, { data, ...config })
  },
}

export default http

sub {
  bottom: -0.25em;
}

sup {
  top: -0.5em;
}

/* Embedded content
   ========================================================================== */

/**
 * Remove the border on images inside links in IE 10.
 */

img {
  border - style: none;
}

/* Forms
   ========================================================================== */

/**
 * 1. Change the font styles in all browsers.
 * 2. Remove the margin in Firefox and Safari.
 */

button,
  input,
  optgroup,
  select,
  textarea {
  margin: 0; /* 2 */
  font - size: 100 %; /* 1 */
  font - family: inherit; /* 1 */
  line - height: 1.15; /* 1 */
}

/**
 * Show the overflow in IE.
 * 1. Show the overflow in Edge.
 */

button,
  input {
  /* 1 */
  overflow: visible;
}

/**
 * Remove the inheritance of text transform in Edge, Firefox, and IE.
 * 1. Remove the inheritance of text transform in Firefox.
 */

button,
  select {
  /* 1 */
  text - transform: none;
}

/**
 * Correct the inability to style clickable types in iOS and Safari.
 */

button,
  [type = 'button'],
  [type = 'reset'],
  [type = 'submit'] {
  appearance: button;
}

/**
 * Remove the inner border and padding in Firefox.
 */

button:: -moz - focus - inner,
  [type = 'button']:: -moz - focus - inner,
    [type = 'reset']:: -moz - focus - inner,
      [type = 'submit']:: -moz - focus - inner {
  padding: 0;
  border - style: none;
}

/**
 * Restore the focus styles unset by the previous rule.
 */

button: -moz - focusring,
  [type = 'button']: -moz - focusring,
    [type = 'reset']: -moz - focusring,
      [type = 'submit']: -moz - focusring {
  outline: 1px dotted ButtonText;
}

/**
 * Correct the padding in Firefox.
 */

fieldset {
  padding: 0.35em 0.75em 0.625em;
}

/**
 * 1. Correct the text wrapping in Edge and IE.
 * 2. Correct the color inheritance from `fieldset` elements in IE.
 * 3. Remove the padding so developers are not caught out when they zero out
 *    `fieldset` elements in all browsers.
 */

legend {
  display: table; /* 1 */
  padding: 0; /* 3 */
  max - width: 100 %; /* 1 */
  white - space: normal; /* 1 */
  color: inherit; /* 2 */
  box - sizing: border - box; /* 1 */
}

/**
 * Add the correct vertical alignment in Chrome, Firefox, and Opera.
 */

progress {
  vertical - align: baseline;
}

/**
 * Remove the default vertical scrollbar in IE 10+.
 */

textarea {
  overflow: auto;
}

/**
 * 1. Add the correct box sizing in IE 10.
 * 2. Remove the padding in IE 10.
 */

[type = 'checkbox'],
  [type = 'radio'] {
  box - sizing: border - box; /* 1 */
  padding: 0; /* 2 */
}

/**
 * Correct the cursor style of increment and decrement buttons in Chrome.
 */

[type = 'number']:: -webkit - inner - spin - button,
  [type = 'number']:: -webkit - outer - spin - button {
  height: auto;
}

/**
 * 1. Correct the odd appearance in Chrome and Safari.
 * 2. Correct the outline style in Safari.
 */

[type = 'search'] {
  appearance: textfield; /* 1 */
  outline - offset: -2px; /* 2 */
}

/**
 * Remove the inner padding in Chrome and Safari on macOS.
 */

[type = 'search']:: -webkit - search - decoration {
  appearance: none;
}

/**
 * 1. Correct the inability to style clickable types in iOS and Safari.
 * 2. Change font properties to `inherit` in Safari.
 */

:: -webkit - file - upload - button {
  appearance: button; /* 1 */
  font: inherit; /* 2 */
}

/* Interactive
   ========================================================================== */

/*
 * Add the correct display in Edge, IE 10+, and Firefox.
 */

details {
  display: block;
}

/*
 * Add the correct display in all browsers.
 */

summary {
  display: list - item;
}

/* Misc
   ========================================================================== */

/**
 * Add the correct display in IE 10+.
 */

template {
  display: none;
}

/**
 * Add the correct display in IE 10.
 */

[hidden] {
  display: none;
}
