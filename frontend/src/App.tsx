import { ConfigProvider } from 'antd'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { RouterProvider } from 'react-router-dom'
import { useAuthBootstrap } from './hooks/use-auth-bootstrap'
import { appRouter } from './router'

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
})

function App() {
  useAuthBootstrap()

  return (
    <ConfigProvider
      theme={{
        token: {
          borderRadius: 18,
          colorBgBase: '#f3efe7',
          colorBgContainer: 'rgba(252, 249, 243, 0.92)',
          colorBorder: '#cabda9',
          colorPrimary: '#1c8074',
          colorInfo: '#1c8074',
          colorSuccess: '#3d8f54',
          colorWarning: '#af6d1c',
          colorError: '#b3412d',
          colorText: '#1a2428',
          colorTextSecondary: '#536168',
          fontFamily: '"Bahnschrift", "Segoe UI Variable", "Trebuchet MS", sans-serif',
          boxShadowSecondary: '0 24px 60px rgba(29, 42, 52, 0.12)',
        },
        components: {
          Button: {
            controlHeight: 40,
          },
          Card: {
            headerFontSize: 15,
          },
          Layout: {
            bodyBg: 'transparent',
            headerBg: 'transparent',
            siderBg: 'transparent',
          },
          Menu: {
            activeBarBorderWidth: 0,
            itemBg: 'transparent',
            itemBorderRadius: 14,
            itemColor: '#536168',
            itemHoverBg: 'rgba(28, 128, 116, 0.08)',
            itemHoverColor: '#102528',
            itemSelectedBg: 'linear-gradient(90deg, rgba(28, 128, 116, 0.18), rgba(28, 128, 116, 0.04))',
            itemSelectedColor: '#102528',
          },
        },
      }}
    >
      <QueryClientProvider client={queryClient}>
        <RouterProvider router={appRouter} />
      </QueryClientProvider>
    </ConfigProvider>
  )
}

export default App
