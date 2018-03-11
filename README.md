# Find-Image
A simple android application that returns images from Imgur based on the search query.

The application uses Imgur API to send search request and receives json response. The results are shown as cards in a recycler view and can be opened to zoom and get details.

Features:
1. Smooth infinite scrolling (Using recyclerview)
2. Real time search with network effeciency (Okhttp library)
3. History of the previous searches (For better user experience, only the last 10 search results are stored)(Room and FloatingSearchView)
4. Image Caching (Fresco)
5. Pan and zoom on any image with next and previous image button (PhotoDraweeView)
6. Get title and url of the images while viewing

Libraries used:
1. FloatingSearchView: https://github.com/arimorty/floatingsearchview
2. OkHttp3: https://github.com/square/okhttp
3. Fresco: https://github.com/square/okhttp
4. PhotoDraweeView: https://github.com/ongakuer/PhotoDraweeView

