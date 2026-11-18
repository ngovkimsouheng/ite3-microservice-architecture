import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import './index.css'
import './i18n.js' // <-- Loads i18next English and Khmer catalogs

ReactDOM.createRoot(document.getElementById('root')).render(
    <React.StrictMode>
       <div>
           <App />
       </div>
    </React.StrictMode>,
)