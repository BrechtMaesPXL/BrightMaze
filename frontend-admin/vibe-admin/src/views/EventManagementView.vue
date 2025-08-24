<template>
    <main>
        <header-component/>
        <HomeComponent></HomeComponent>
        <EventFormComponent class="event-form"/>
        <EventListComponent class="event-list"/>
        
    </main>

</template>

<script setup>
import HeaderComponent from "@/components/HeaderComponent.vue";
import EventFormComponent from "@/components/EventFormComponent.vue";
import EventListComponent from "@/components/EventListComponent.vue";
import HomeComponent from "@/components/HomeComponent.vue";

</script>


<style scoped>
h1 {
    color: lightskyblue;
    font-size: 40pt;
    text-align: center;
    background-color: white;
    width: 60%;
    margin: 0 auto;
    border-radius: 2rem;
}

.event-form {
    float: left;
    top: 20px;
    align-self: flex-start;
    margin-left: 5%;
    width: 25%;
    margin-top: 15px;
}

.event-list {
    float: right;
    margin-right: 10%;
    width: 57%;
    overflow-y: auto;
    max-height: 92vh;
}
.backbtn{
    position: absolute;   
    top: 20px;            
    left: 20px;           
    padding: 10px 20px;   
    background-color: white; 
    color: blue;        
    font-size: 20px;      
    border: none;         
    border-radius: 5px;  
    cursor: pointer;    
    transition: box-shadow 0.3s ease;
    font-family:Verdana, Geneva, Tahoma, sans-serif 
    
}

.backbtn:hover {
    box-shadow: 0 10px 20px rgba(0,0,0,0.2) !important;
}
</style>