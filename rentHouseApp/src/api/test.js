import { loginApi, roomApi, apartmentApi, appointmentApi, historyApi, regionApi } from './index'

// 测试所有API接口
const testAllApis = async () => {
  console.log('开始测试API接口...')
  
  try {
    // 测试地区接口
    console.log('\n1. 测试地区接口:')
    const provinces = await regionApi.listProvince()
    console.log('省份列表:', provinces)
    
    if (provinces && provinces.length > 0) {
      const cities = await regionApi.listCityByProvinceId(provinces[0].id)
      console.log('城市列表:', cities)
      
      if (cities && cities.length > 0) {
        const districts = await regionApi.listDistrictByCityId(cities[0].id)
        console.log('区县列表:', districts)
      }
    }
    
    // 测试房间接口
    console.log('\n2. 测试房间接口:')
    const rooms = await roomApi.pageRoomItem(1, 10, {})
    console.log('房间列表:', rooms)
    
    if (rooms && rooms.records && rooms.records.length > 0) {
      const roomDetail = await roomApi.getRoomDetail(rooms.records[0].id)
      console.log('房间详情:', roomDetail)
    }
    
    // 测试公寓接口
    console.log('\n3. 测试公寓接口:')
    // 假设我们有一个公寓ID
    // const apartmentDetail = await apartmentApi.getApartmentDetail(1)
    // console.log('公寓详情:', apartmentDetail)
    
    console.log('\nAPI测试完成!')
  } catch (error) {
    console.error('API测试失败:', error)
  }
}

testAllApis()