

/*
    -> code tangling ( tight coupling )
    -> code scattering ( code is scattered across the codebase )
*/


function hello(){
    console.log("Hello")
}
function hi(){
    console.log("Hi")
}
function hey(){
    console.log("Hey")
}

// Aspect
function withEmoji(func){
    return function(){
        func()
        console.log("😀")
    }
}
function withAuth(func){
    return function(){
        console.log("👮‍♀️")
        func()
    }
}

let helloWithEmojiAndAuth = withEmoji(withAuth(hello));
helloWithEmojiAndAuth();