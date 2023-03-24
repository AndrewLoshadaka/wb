import React, { useEffect, useState } from 'react';
import logo from './logo.svg';
import './App.css';

const App = () => {

   const [reviews, setReviews] = useState([]);
   const [loading, setLoading] = useState(false);

   useEffect(() => {
     setLoading(true);

     fetch('/api/reviews')
       .then(response => response.json())
       .then(data => {
         setReviews(data);
         setLoading(false);
       })
   }, []);

   if (loading) {
     return <p>Loading...</p>;
   }

   return (
     <div className="App">
       <header className="App-header">
         <img src={logo} className="App-logo" alt="logo" />
         <div className="App-intro">
           <h2>Reviews List</h2>
           {reviews.map(review =>
             <div key={review.id}>
               {review.author}
             </div>
           )}
         </div>
       </header>
     </div>
   );
 }

export default App;
